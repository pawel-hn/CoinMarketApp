package pawel.hn.coinmarketapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.databinding.FragmentDialogAddEditBinding
import pawel.hn.coinmarketapp.viewmodels.AddCoinViewModel

@AndroidEntryPoint
class AddCoinFragmentDialog : DialogFragment() {


    private lateinit var binding: FragmentDialogAddEditBinding
    private var coinName: String = ""
    private var coinVolume: Double = 0.0

    private val viewModel: AddCoinViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogAddEditBinding.inflate(inflater, container, false)
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.dialog_round_corners)

        val walletNo = AddCoinFragmentDialogArgs.fromBundle(requireArguments()).walletNumber

        val spinnerAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            viewModel.coinsNamesList()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        binding.apply {

            spinnerDialogSearch.apply {
                adapter = spinnerAdapter
                onItemSelectedListener = spinnerCoinSelected
            }

            btnDialogCancel.setOnClickListener {
                dismiss()
            }

            btnDialogSave.setOnClickListener {
                if (editTextVolume.text!!.isBlank()) {
                    Toast.makeText(requireContext(), R.string.dialog_error, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    coinVolume = editTextVolume.text.toString().toDouble()
                    val coinWallet = viewModel.createWalletCoin(coinName, coinVolume, walletNo)
                    viewModel.addToWallet(coinWallet)
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