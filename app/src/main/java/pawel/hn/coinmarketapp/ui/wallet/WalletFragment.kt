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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.databinding.FragmentWalletBinding
import pawel.hn.coinmarketapp.ui.addeditdialog.AddCoinFragmentDialogDirections
import pawel.hn.coinmarketapp.util.WALLET_NO
import pawel.hn.coinmarketapp.util.showLog

@AndroidEntryPoint
class WalletFragment : Fragment(R.layout.fragment_wallet) {

    private val viewModel: WalletViewModel by viewModels()

    lateinit var adapter: WalletAdapter
    lateinit var binding: FragmentWalletBinding
    private var walletNo: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWalletBinding.bind(view)
        adapter = WalletAdapter()

        walletNo = requireArguments()[WALLET_NO] as Int
        showLog("walletFragment: $walletNo")

        binding.apply {
            btnAddCoin.setOnClickListener {
               walletNo?.let {
                   showLog("add clicked: $walletNo")
                   val action =
                       AddCoinFragmentDialogDirections.actionGlobalAddCoinFragmentDialog(walletNo!!)
                   view.findNavController().navigate(action)
               }
            }
            recyclerViewWallet.adapter = adapter
            recyclerViewWallet.itemAnimator = null

            ItemTouchHelper(swipe).attachToRecyclerView(recyclerViewWallet)
        }

        subscribeToObservers()

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


    private fun subscribeToObservers() {
        viewModel.walletList.observe(viewLifecycleOwner) { allWallets ->
            val specificWalletList = allWallets.filter { it.walletNo == walletNo }
            binding.textViewBalance.text = viewModel.calculateTotal(specificWalletList)

            adapter.submitList(specificWalletList)
        }

        viewModel.coinList.observe(viewLifecycleOwner) {
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