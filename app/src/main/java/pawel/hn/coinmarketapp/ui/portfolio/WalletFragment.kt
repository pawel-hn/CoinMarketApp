package pawel.hn.coinmarketapp.ui.portfolio

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import pawel.hn.coinmarketapp.CoinsApplication
import pawel.hn.coinmarketapp.R
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
        val adapter = WalletAdapter()

        binding.apply {
            btnAddCoin.setOnClickListener {
                val action = WalletFragmentDirections
                    .actionWalletFragmentToAddCoinFragmentDialog()
                findNavController().navigate(action)
            }
            recyclerViewWallet.adapter = adapter

            ItemTouchHelper(object: ItemTouchHelper
            .SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val coin = adapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(coin)
                }
            }).attachToRecyclerView(recyclerViewWallet)

        }
        viewModel.walletList.observe(viewLifecycleOwner ) {
            binding.textViewBalance.text = viewModel.calculateTotal()
            adapter.submitList(it)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_wallet, menu)
    }

}