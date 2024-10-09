package com.nikohy.barcodereader

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager

// Intent identifiers
const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"

private const val TAG = "MainActivity"

/**
 * main activity class
 */
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var dataDataViewModel: ScannerDataViewModel
//    private lateinit var db: JsonDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // hide action bar, we will be using fragment views with Toolbar instead
        supportActionBar?.hide()

        // Shared data view model
        dataDataViewModel = ViewModelProvider(this)[ScannerDataViewModel::class.java]

//        db = JsonDB(applicationContext) // eg. DBHelper
//        lifecycle.addObserver(db) // Activity

        // receive and process intent data
        // https://developer.android.com/training/sharing/receive
//        when (intent?.action) {
//            Intent.ACTION_SEND -> {
//                // handle single image being sent
//                if (intent.type?.startsWith("image/") == true) {
//                    handleSendImage(this, intent)
//                }
//                // else if(intent.type == "text/plain") { handleSendText(intent) }
//            }
//            // TODO support for multiple images
//            /*Intent.ACTION_SEND_MULTIPLE -> {
//                // handle multiple images being sent
//                if (intent.type?.startsWith("image/") == true) {
//                    handleSendMultipleImages(intent)
//                }
//            }*/
//            else -> {
//                // handle other intents
//            }
//        }

        // Request camera permission
        PermissionHelper.checkAndRequestPermissions(
            this,
            listOf(
                Manifest.permission.CAMERA, // Camera
                Manifest.permission.WRITE_EXTERNAL_STORAGE, // SQLite
                Manifest.permission.READ_EXTERNAL_STORAGE // SQLite
            )
        )

    }

    /** When key down event is triggered, relay it via local broadcast so any fragments can handle it */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                // ScannerFragment toggle torchlight on volume events
                if (::dataDataViewModel.isInitialized && dataDataViewModel.isScannerActive) {
                    val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    true // consume this event
                } else {
                    super.onKeyDown(keyCode, event)
                }
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // this will be called when permissions are granted or denied by user
        Log.d(TAG, "TEST onRequestPermissionsResult, $requestCode, $permissions, $grantResults")
    }

// listen for volume up or down events to toggle flashlight when camera is active
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            if (ScannerFragment.isActive && ScannerFragment.camera != null) {
//                val flashState = !ScannerFragment.isFlashActive
//                val control = ScannerFragment.camera!!.cameraControl
//                control.enableTorch(flashState)
//                ScannerFragment.isFlashActive = flashState
//                return true // stop normal event we handle this case
//            }
//        }
//        // else call normal event to increase or decrease volume or any other key
//        return super.onKeyDown(keyCode, event)
//    }

//    // handle single image being sent by intent
//    private fun handleSendImage(activity: Activity, intent: Intent) {
//        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { imageUri ->
//            // Update UI to reflect image being shared
//            Log.d("BarcodeReader", "Received by Intent: $imageUri")
//            //Log.d("BarcodeReader", intent.toString())
//            // run in separated thread
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    SharedData.imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                        ImageDecoder.decodeBitmap(
//                            ImageDecoder.createSource(
//                                contentResolver,
//                                imageUri
//                            )
//                        )
//                    } else {
//                        MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                    }
//                } catch (ex: Exception) {
//                    Log.e("BarcodeReader", "Image parse failed: $ex")
//                    Toast.makeText(
//                        applicationContext,
//                        "Failed to parse shared image!",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//        }
//    }
//
//    // handle multiple images being sent by intent
//    private fun handleSendMultipleImages(intent: Intent) {
//        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
//            // Update UI to reflect multiple images being shared
//        }
//    }
//
//    // handle raw text being sent by intent
//    private fun handleSendText(intent: Intent) {
//        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
//            // Update UI to reflect text being shared
//        }
//    }
}