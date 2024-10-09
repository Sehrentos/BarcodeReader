package com.nikohy.barcodereader

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager

private const val TAG = "ScannerFragment"

/**
 * CameraX with vision scanner [Fragment] subclass.
 */
class ScannerFragment : Fragment() {

    companion object { // export static public data

        private const val REQUEST_CAMERA_PERMISSION = 10

//        var collected: ArrayList<Barcode> = arrayListOf() // current collected barcodes
//        var isActive = false // camera active status

//        private var onUpdate: ((index: Int, barcode: Barcode) -> Unit)? = null
//        fun setUpdater(func: (index: Int, barcode: Barcode) -> Unit) {
//            this.onUpdate = func
//        }
    }

    private lateinit var camera: Camera // current camera instance
    private lateinit var textureView: PreviewView
    //private lateinit var textScanResult: TextView
    private lateinit var broadcastManager: LocalBroadcastManager

    // Shared data view model
    private lateinit var scannerDataViewModel: ScannerDataViewModel

    /** Volume down button receiver used to trigger torch light */
    private val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                // When the volume up or down button is pressed, toggle torch
                KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                    scannerDataViewModel.isTorchOn = !scannerDataViewModel.isTorchOn
                    camera.cameraControl.enableTorch(scannerDataViewModel.isTorchOn)
                    Log.d(TAG, "Torch status ${scannerDataViewModel.isTorchOn}")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.apply {
            actionBar?.apply {
                //setDisplayShowCustomEnabled(true)
                //setCustomView(R.layout.action_bar)
                hide()
            }
        }
        // set view model to share data between fragments
        scannerDataViewModel = ViewModelProvider(requireActivity()).get(ScannerDataViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scannerDataViewModel.isScannerActive = false

        // Unregister the broadcast receivers and listeners
        broadcastManager.unregisterReceiver(volumeDownReceiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        broadcastManager = LocalBroadcastManager.getInstance(view.context)

        // Set up the intent filter that will receive events from our main activity
        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        broadcastManager.registerReceiver(volumeDownReceiver, filter)

        textureView = view.findViewById(R.id.cameraPreviewView)
        //textScanResult = view.findViewById(R.id.textScanResult)

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            textureView.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    // 480, 640
    // 720, 1280
    // 1200, 1600
    // 1440, 2560
    // increase resolution to get better scan results, but it will slow down the process
    private fun getTargetResolution(): Size {
        val w = 720 // WIDTH in portrait mode, HEIGHT in landscape
        val h = 1280 // HEIGHT in portrait mode, WIDTH in landscape
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> Size(w, h)
            Configuration.ORIENTATION_LANDSCAPE -> Size(h, w)
            else -> Size(h, w)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera() {
        scannerDataViewModel.isScannerActive = true
        //textScanResult.text = ""
        val targetResolution = getTargetResolution()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraSelector =
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val previewConfig = Preview.Builder()
            // We want to show input from back camera of the device
            .setTargetResolution(targetResolution)
            .build()

        previewConfig.setSurfaceProvider(textureView.surfaceProvider)

        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetResolution(targetResolution)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits requested capture mode
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .build()
        val executor = ContextCompat.getMainExecutor(requireContext())
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also {
                it.setAnalyzer(executor, CodeAnalyzer { barcodes ->
                    if (barcodes == null) return@CodeAnalyzer
                    for (code in barcodes) {
                        // collect only unique raw values
                        //val lastIndex = scannerDataViewModel.mutableListBarcodes.size - 1
                        if (scannerDataViewModel.barcodes.value == null || scannerDataViewModel.barcodes.value?.none { e -> e.rawValue == code.rawValue } == true) {
                            scannerDataViewModel.addBarcode(
                                ScannerDataViewModel.Barcode(
                                    format = code.format,
                                    rawValue = code.rawValue ?: ""
                                )
                            )
//                        scannerDataViewModel.onBarcodeResultUpdate?.invoke(
//                            scannerDataViewModel.mutableListBarcodes.size - 1,
//                            code
//                        )
                            val result = "${scannerDataViewModel.barcodes.value?.size}. ${code.rawValue}"
                            //textScanResult.text = result
                            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()

                            // option 2. pass data to previous fragment
                            // Use the Kotlin extension in the fragment-ktx artifact (sdk 31 needed)
                            // post result back to parent fragment listener
                            //setFragmentResult("requestBarcodeKey", bundleOf("barcode" to code.rawValue))
                        }
                    }
                })
            }

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewConfig,
                imageCapture,
                imageAnalyzer
            )

            // set flash on by default
            camera.cameraControl.enableTorch(scannerDataViewModel.isTorchOn)

            // option 1. set auto focus on tap
            textureView.afterMeasured {
                textureView.setOnTouchListener { _, event ->
                    return@setOnTouchListener when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            true
                        }

                        MotionEvent.ACTION_UP -> {
                            val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                                textureView.width.toFloat(), textureView.height.toFloat()
                            )
                            val autoFocusPoint = factory.createPoint(event.x, event.y)
                            try {
                                camera.cameraControl.startFocusAndMetering(
                                    FocusMeteringAction.Builder(
                                        autoFocusPoint,
                                        FocusMeteringAction.FLAG_AF
                                    ).apply {
                                        //focus only when the user tap the preview
                                        disableAutoCancel()
                                    }.build()
                                )
                            } catch (e: CameraInfoUnavailableException) {
                                Log.d("ERROR", "cannot access camera", e)
                            }
                            true
                        }

                        else -> false // Unhandled event.
                    }
                }
            }
            // option 2. auto focus on every X seconds
            //textureView.afterMeasured {
            //     val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1f, 1f)
            //             .createPoint(.5f, .5f)
            //     try {
            //         val autoFocusAction = FocusMeteringAction.Builder(
            //             autoFocusPoint,
            //             FocusMeteringAction.FLAG_AF
            //         ).apply {
            //             //start auto-focusing after 2 seconds
            //             setAutoCancelDuration(2, TimeUnit.SECONDS)
            //         }.build()
            //         camera.cameraControl.startFocusAndMetering(autoFocusAction)
            //     } catch (e: CameraInfoUnavailableException) {
            //         Log.d("ERROR", "cannot access camera", e)
            //     }
            // }

        }, executor)
    }

    // helper extension function for utility
    private inline fun View.afterMeasured(crossinline block: () -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }

    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult( // onRequestPermissionsResult
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (isCameraPermissionGranted()) {
                textureView.post { startCamera() }
            } else {
                Toast.makeText(context, "Camera permission is required.", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed() // onBackPressed()
            }
        }
    }
}