package pawel.hn.coinmarketapp.ui.addeditdialog


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import pawel.hn.coinmarketapp.CoinsApplication
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.databinding.FragmentDialogAddEditBinding


class AddCoinFragmentDialog : DialogFragment() {

    private lateinit var args: AddCoinFragmentDialogArgs
    private lateinit var binding: FragmentDialogAddEditBinding
    private var coinName: String = ""
    private var coinVolume: String = ""

    private val viewModel: AddCoinViewModel by viewModels {
        AddCoinViewModel.AddCoinViwModelFactory(
            (this.requireActivity().application as CoinsApplication).repository
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = AddCoinFragmentDialogArgs.fromBundle(requireArguments())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View{
        binding = FragmentDialogAddEditBinding.inflate(inflater, container, false)
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.dialog_round_corners)

        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            viewModel.coinsNamesList()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        binding.apply {

            spinnerDialogSearch.adapter = adapter
            spinnerDialogSearch.onItemSelectedListener = spinnerCoinSelected

            btnDialogCancel.setOnClickListener {
                dismiss()
            }
            btnDialogSave.setOnClickListener {
                if (editTextVolume.text!!.isBlank()) {
                    Toast.makeText(requireContext(), R.string.dialog_error, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    coinVolume = editTextVolume.text.toString()
                    viewModel.addToWallet(coinName,coinVolume)
                    dismiss()
                }
            }
        }
        return binding.root
    }

    private val spinnerCoinSelected = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            coinName = parent?.getItemAtPosition(position).toString()
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }


}