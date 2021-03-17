package pawel.hn.coinmarketapp.ui.wallet

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.databinding.FragmentWalletBinding
import pawel.hn.coinmarketapp.showLog

@AndroidEntryPoint
class WalletFragment : Fragment(R.layout.fragment_wallet) {

    private val viewModel: WalletViewModel by viewModels()

    lateinit var adapter: WalletAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentWalletBinding.bind(view)
        adapter = WalletAdapter()

        binding.apply {
            btnAddCoin.setOnClickListener {
                val action = WalletFragmentDirections
                    .actionWalletFragmentToAddCoinFragmentDialog()
                findNavController().navigate(action)
            }
            recyclerViewWallet.adapter = adapter
            recyclerViewWallet.itemAnimator = null

            ItemTouchHelper(swipe).attachToRecyclerView(recyclerViewWallet)
        }
        viewModel.walletList.observe(viewLifecycleOwner) {
            showLog("wallet walletList observer called")
            binding.textViewBalance.text = viewModel.calculateTotal(it)
            adapter.submitList(it)
        }

        viewModel.coinList.observe(viewLifecycleOwner) {
            showLog("wallet coinList observer called")
            viewModel.walletRefresh(it)
        }

        viewModel.eventRefresh.observe(viewLifecycleOwner) { event ->
            binding.apply {
                if (event) {
                    recyclerViewWallet.visibility = View.GONE
                    progressBarWallet.visibility = View.VISIBLE
                } else {
                    recyclerViewWallet.visibility = View.VISIBLE
                    progressBarWallet.visibility = View.GONE
                }
            }

        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_wallet, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_wallet_refresh -> {
                viewModel.refreshData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val swipe = object : ItemTouchHelper
    .SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val coin = adapter.currentList[viewHolder.adapterPosition]
            viewModel.onTaskSwiped(coin)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {

            val itemView = viewHolder.itemView
            val drawable = ColorDrawable()
            val colorRed = Color.parseColor("#F5160A")
            drawable.color = colorRed
            drawable.setBounds(
                itemView.right + dX.toInt(),
                itemView.top, itemView.right, itemView.bottom
            )
            drawable.draw(c)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }

}