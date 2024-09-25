package com.nikohy.barcodereader

import android.annotation.SuppressLint
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Vision barcode analyzer class
 *
 * @param onQrCodesDetected callback function to be called when QR codes are detected
 */
class CodeAnalyzer(
    private val onQrCodesDetected: (qrCodes: List<Barcode>?) -> Unit
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        // Prepare the input image
        // https://developers.google.com/ml-kit/vision/barcode-scanning/android#2.-prepare-the-input-image
        val mediaImage = image.image ?: return image.close()
        val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

        // https://developers.google.com/ml-kit/vision/barcode-scanning/android#1.-configure-the-barcode-scanner
        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_ITF,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_PDF417,
            Barcode.FORMAT_AZTEC,
            Barcode.FORMAT_DATA_MATRIX,
        ).build()

        // create BarcodeScanner from options
        val scanner = BarcodeScanning.getClient(options)
        scanner.process(inputImage)
            .addOnCompleteListener { barcodes ->
                /*barcodes.result?.forEach { barcode ->
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints
                    val rawValue = barcode.rawValue
                }*/
                onQrCodesDetected(barcodes.result)
                //img.close()
                image.close()
                scanner.close()
            }
            .addOnFailureListener { failure ->
                failure.printStackTrace()
                //img.close()
                image.close()
                scanner.close()
            }
    }

    // version androidx.camera:camera-core:1.2.2
    // fix issue with an error:
    // java.lang.AbstractMethodError: abstract method "android.util.Size androidx.camera.core.ImageAnalysis$Analyzer.getDefaultTargetResolution()"
    override fun getDefaultTargetResolution(): Size {
        return Size(640, 480) // Change this to the desired target resolution
    }
}