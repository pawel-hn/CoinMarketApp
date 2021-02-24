package pawel.hn.coinmarketapp.ui.portfolio

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import pawel.hn.coinmarketapp.CoinsApplication
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.databinding.FragmentWalletBinding


class WalletFragment : Fragment(R.layout.fragment_wallet) {

    private val viewModel: WalletViewModel by viewModels {
        WalletViewModel.WalletViewModelFactory(
            (this.requireActivity().application as CoinsApplication).repository
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentWalletBinding.bind(view)

        binding.btnAddCoin.setOnClickListener {
            val action = WalletFragmentDirections
                .actionWalletFragmentToAddCoinFragmentDialog(null, viewModel.coinsNamesList())
            findNavController().navigate(action)
        }

        viewModel.walletList.observe(viewLifecycleOwner ) {
            Log.d(TAG,"walletList, ${it.size}")
            binding.textViewBalance.text = viewModel.calculateTotal()
            val adapter = WalletAdapter(it)
            binding.recyclerViewWallet.adapter = adapter
        }
    }

}