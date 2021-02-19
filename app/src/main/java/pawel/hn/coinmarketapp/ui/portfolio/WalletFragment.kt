package pawel.hn.coinmarketapp.ui.portfolio

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import pawel.hn.coinmarketapp.CoinsApplication
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.databinding.FragmentWalletBinding


class WalletFragment() : Fragment(R.layout.fragment_wallet) {

    private val list = arrayOf("aaa","bbb","ccc","ddd")
    private val viewmodel: WalletViewModel by viewModels {
        WalletViewModel.WalletViewModelFactory(
            (this.requireActivity().application as CoinsApplication).repository
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentWalletBinding.bind(view)

        binding.btnAddCoin.setOnClickListener {
            val action = WalletFragmentDirections
                .actionWalletFragmentToAddCoinFragmentDialog(null, viewmodel.coinsNamesList())
            findNavController().navigate(action)

        }

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFragment.adapter = adapter
    }
}