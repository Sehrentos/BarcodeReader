package com.nikohy.barcodereader

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.barcode.common.Barcode

/**
 * RecyclerView adapter for displaying scanned barcode results
 */
class ScannedRecyclerAdapter(
    private val barcodes: MutableList<ScannerDataViewModel.Barcode>
) : RecyclerView.Adapter<ScannedRecyclerAdapter.BarcodeHolder>() {

    override fun getItemCount() = barcodes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return BarcodeHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: BarcodeHolder, position: Int) {
        val barcodeItem = barcodes[position]
        holder.bindBarcode(barcodeItem)
    }

    class BarcodeHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view = v
        private val context = v.context
        private var barcode: ScannerDataViewModel.Barcode? = null

        //private var imageView: ImageView = view.findViewById(R.id.list_item_imageView)
        private var textView1: TextView = view.findViewById(R.id.list_item_textView1)
        private var textView2: TextView = view.findViewById(R.id.list_item_textView2)

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            //Log.d("RecyclerView", "CLICK view!")

            // when view clicked is R.id.list_item_imageView_delete handle delete of item and recycler view update
//            if (v?.id == R.id.list_item_imageView_delete) {
//                Log.d("RecyclerView", "CLICK delete!")
//                barcode?.let {
//                    //Log.d("RecyclerView", "CLICK delete! ${it.rawValue}")
//                    // remove item from list
//                    val index = adapterPosition
//                    val itemCount = barcode?.let { barcodes.removeBarcode(it) } ?: 0
//                    // update recycler view
//                    notifyItemRemoved(index)
//                    notifyItemRangeChanged(index, itemCount)
//                }
//                return
//            }

            // copy barcode.rawValue to clipboard
            val barcodeValue = this.barcode?.rawValue ?: return
            val clipboard: ClipboardManager? = getSystemService(context, ClipboardManager::class.java)
            val clip = ClipData.newPlainText("Scanned barcode", barcodeValue)
            clipboard?.setPrimaryClip(clip)
            // https://developer.android.com/develop/ui/views/touch-and-input/copy-paste#Feedback
            // Provide feedback when copying to the clipboard
            // Users expect visual feedback when an app copies content to the clipboard.
            // This is done automatically for users in Android 13 and higher, but it must be manually implemented in prior versions.
            // Starting in Android 13, The new confirmation does the following:
            // - Confirms the content was successfully copied.
            // - Provides a preview of the copied content.
            // Only show a toast for Android 12 and lower.
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
            }
        }

        fun bindBarcode(barcode: ScannerDataViewModel.Barcode) {
            this.barcode = barcode
            //imageView
            textView1.text = formatToName(barcode.format)
            textView2.text = barcode.rawValue
        }

        private fun formatToName(format: Int): String {
            return when (format) {
                Barcode.FORMAT_CODE_128 -> "CODE 128"
                Barcode.FORMAT_CODE_39 -> "CODE 39"
                Barcode.FORMAT_CODE_93 -> "CODE 93"
                Barcode.FORMAT_CODABAR -> "CODABAR"
                Barcode.FORMAT_EAN_13 -> "EAN 13"
                Barcode.FORMAT_EAN_8 -> "EAN 8"
                Barcode.FORMAT_ITF -> "ITF"
                Barcode.FORMAT_UPC_A -> "UPC A"
                Barcode.FORMAT_UPC_E -> "UPC E"
                Barcode.FORMAT_QR_CODE -> "QR CODE"
                Barcode.FORMAT_PDF417 -> "PDF 417"
                Barcode.FORMAT_AZTEC -> "AZTEC"
                Barcode.FORMAT_DATA_MATRIX -> "DATA MATRIX"
                else -> "UNKNOWN"
            }
        }

        /*companion object {
            private val BARCODE_KEY = "BARCODE"
        }*/
    }
}