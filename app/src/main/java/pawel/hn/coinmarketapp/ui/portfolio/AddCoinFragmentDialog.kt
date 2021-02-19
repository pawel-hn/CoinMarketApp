package pawel.hn.coinmarketapp.ui.portfolio


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.DialogFragment

import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.databinding.FragmentDialogAddEditBinding


class AddCoinFragmentDialog() : DialogFragment() {

    private lateinit var args: AddCoinFragmentDialogArgs
    private lateinit var binding: FragmentDialogAddEditBinding
    private lateinit var builder: AlertDialog.Builder
    private var chosenCoin: String? = null

    private val list = arrayOf("aaa", "bbb", "ccc", "ddd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        args = AddCoinFragmentDialogArgs.fromBundle(requireArguments())
        Log.d(TAG, "onCreate dialog: ${args.coinsNamesArray?.size ?: "args are null"}")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView called")
        binding = FragmentDialogAddEditBinding.inflate(inflater, container, false)
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.dialog_round_corners)
           searchSpinner()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            args.coinsNamesArray!!
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.apply {
          //  spinnerDialog.adapter = adapter
            spinnerDialogSearch.adapter = adapter
            btnDialogCancel.setOnClickListener {
                Log.d(TAG, "cancel called")
                Toast.makeText(
                    parentFragment?.requireContext(),
                    "cancel clikked",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
            btnDialogSave.setOnClickListener {
                Log.d(TAG, "save called")
                dismiss()
            }
        }

        return binding.root
    }

    private fun searchSpinner() {
        val searchMethod = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item,
            list)
        searchMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDialogSearch.adapter = searchMethod
    }

}