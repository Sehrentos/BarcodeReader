package com.nikohy.barcodereader

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    // Shared data view model
    private lateinit var scannerDataViewModel: ScannerDataViewModel
    private val barcodes = mutableListOf<ScannerDataViewModel.Barcode>()

    // region UI variables
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterScanned: ScannedRecyclerAdapter
    lateinit var btnScan: Button
    lateinit var btnReset: Button
    // endregion

    // region database variables
//    private lateinit var db: JsonDB
//    private lateinit var records: RecordsTable
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set view model to share data between fragments
        scannerDataViewModel = ViewModelProvider(requireActivity())[ScannerDataViewModel::class.java]

        // region database init
        // Database V2 sample 21.01.2022
//        db = JsonDB(requireContext().applicationContext)
//        records = RecordsTable(db)
//        lifecycle.addObserver(records) // register lifecycle observer for fragment event
//        scannerDataViewModel.barcodes.value = records.getByClass<ScannerDataViewModel.Barcode>().toMutableList().reversed() // get initial values
        // endregion

        // region TODO some future version perhaps?
//        // Use the Kotlin extension in the fragment-ktx artifact
//        // listen any for any child fragment request post with given request key
//        setFragmentResultListener("requestBarcodeKey") { requestKey, bundle ->
//            // We use a String here, but any type that can be put in a Bundle is supported
//            val result = bundle.getString("barcode")
//            // Do something with the result
//            Log.d(javaClass.simpleName, "fragment result listener: $requestKey / $result")
//        }
        // endregion
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        btnScan = view.findViewById(R.id.btn_scan)
        btnReset = view.findViewById(R.id.btn_reset)

        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        adapterScanned = ScannedRecyclerAdapter(barcodes)
        recyclerView.adapter = adapterScanned

        // observe data model changes and update list
        scannerDataViewModel.barcodes.observe(viewLifecycleOwner) {
            barcodes.clear()
            barcodes.addAll(it)
            adapterScanned.notifyItemRangeChanged(0, barcodes.size)
            Log.d(javaClass.simpleName, "UPDATE barcodes list ${barcodes.size}")
        }

        // Begin scan and navigate to ScannerFragment
        btnScan.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_scannerFragment
            )
        }

        // Reset scanned result
        btnReset.setOnClickListener {
            //resetResponse()
            val itemCount = scannerDataViewModel.barcodes.value?.size ?: 0
            scannerDataViewModel.clearBarcodes()
            //adapterScanned.notifyDataSetChanged()
            adapterScanned.notifyItemRangeRemoved(0, itemCount)
        }

    }
}