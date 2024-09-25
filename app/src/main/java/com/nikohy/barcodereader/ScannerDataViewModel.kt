package com.nikohy.barcodereader

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nikohy.barcodereader.database.JsonDB
import com.nikohy.barcodereader.database.LIFETIME
import com.nikohy.barcodereader.database.RecordsTable
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// use view model to share data between fragments etc.
// instantiate like this:
// val viewModel = ViewModelProvider(requireActivity()).get(ScannerViewModel::class.java)
class ScannerDataViewModel(
    application: Application
) : AndroidViewModel(application) {

    // region database variables
    private var db = JsonDB(application.applicationContext)
    private var records = RecordsTable(db)
    // endregion

    // camera active status
    var isScannerActive = false

    // is torchlight on
    var isTorchOn = false

    // list of analyzed barcodes
    val barcodes = MutableLiveData<List<Barcode>>()

    init {
        // get from SQL, but work in the IO thread
        CoroutineScope(Dispatchers.IO).launch { // IO thread
            val rows = records.getByClass<Barcode>().toMutableList().reversed()
            Log.d("BarcodeReaderAppDataViewModel", "init count ${rows.size}")
            CoroutineScope(Dispatchers.Main).launch { // Main thread
                barcodes.value = rows
            }
        }
    }

    /**
     * Set new barcode to the collected list and notify observers
     */
    fun addBarcode(vararg codes: Barcode) {
        val list = mutableListOf<Barcode>()
        list.addAll(codes)
        barcodes.value?.let { list.addAll(it) }
        barcodes.value = list

        // add to SQL, but work in the IO thread
        CoroutineScope(Dispatchers.IO).launch {
            codes.forEach {
                records.add<Barcode>(it, LIFETIME.INFINITE)
            }
        }
    }

    /**
     * TODO: Delete barcode by index from the collection and notify observers
     */
//    fun delBarcode(index: Int) {
//        val list = barcodes.value?.toMutableList()
//        list?.removeAt(index)
//        barcodes.value = list
//
//        // remove from SQL, but work in the IO thread
//        CoroutineScope(Dispatchers.IO).launch {
//            records.delByClass<Barcode>()
//            //records.delById()
//        }
//    }

    /**
     * Remove all barcodes from the collection and notify observers
     */
    fun clearBarcodes() {
        barcodes.value = arrayListOf()

        // remove from SQL, but work in the IO thread
        CoroutineScope(Dispatchers.IO).launch {
            records.delByClass<Barcode>()
        }
    }

    // callback used to receive analyzed Barcode from the scanner fragment
    // TODO replace with getBarcodes().observe(this, Observer<List<Barcode>>{ code -> }
    var onBarcodeResultUpdate: ((index: Int, barcode: Barcode) -> Unit)? = null
    fun setOnBarcodeResultUpdateListener(callback: (Int, Barcode) -> Unit) {
        onBarcodeResultUpdate = callback
    }

    @Serializable
    data class Barcode(val format: Int = -1, val rawValue: String = "") {}

//    // tests below, pass any data
//    private var data: Any? = null
//
//    fun set(value: Any) {
//        data = value
//    }
//
//    fun get(): Any? {
//        return data
//    }
}