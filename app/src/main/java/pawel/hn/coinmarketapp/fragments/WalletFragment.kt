package pawel.hn.coinmarketapp.fragments

import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.databinding.FragmentWalletBinding
import pawel.hn.coinmarketapp.adapters.WalletAdapter
import pawel.hn.coinmarketapp.viewmodels.WalletViewModel
import pawel.hn.coinmarketapp.util.*

@AndroidEntryPoint
class WalletFragment : Fragment(R.layout.fragment_wallet) {

    private val viewModel: WalletViewModel by viewModels()

    lateinit var adapter: WalletAdapter
    lateinit var binding: FragmentWalletBinding
    private var walletNo: Int? = null
    private var currency = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


         val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
         currency = sharedPreferences.getString(
            context?.getString(R.string.settings_currency_key),
            CURRENCY_USD
        )!!

        binding = FragmentWalletBinding.bind(view)
        adapter = WalletAdapter()

        walletNo = requireArguments()[WALLET_NO] as Int

        binding.apply {

            btnAddCoin.setOnClickListener {
               walletNo?.let {
                   val action =
                       AddCoinFragmentDialogDirections.actionGlobalAddCoinFragmentDialog(walletNo!!)
                   view.findNavController().navigate(action)
               }
            }
            lifecycleOwner = this@WalletFragment
            walletViewModel = viewModel
            recyclerViewWallet.adapter = adapter
            recyclerViewWallet.itemAnimator = null

            if (walletNo!! < 3) {
                ItemTouchHelper(swipe).attachToRecyclerView(recyclerViewWallet)
            }

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
                viewModel.refreshData(currency)
            }
            R.id.menu_wallet_delete_all -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.clear_wallet))
                    .setMessage(getString(R.string.clear_wallet_msg))
                    .setPositiveButton(R.string.yes) {_, _ ->
                        viewModel.deleteAll()
                    }
                    .setNegativeButton(R.string.no){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun subscribeToObservers() {
        viewModel.walletLiveData.observe(viewLifecycleOwner) { allWallets ->

            adapter.setCurrency(currency)

            val specificWalletList = if (walletNo == 3) {
                viewModel.totalWallet(allWallets)
            } else {
                allWallets.filter { it.walletNo == walletNo }
            }


            val total = viewModel.calculateTotalBalance(specificWalletList)
            viewModel.setChart(specificWalletList, binding.chart, requireContext())
            binding.textViewBalance.text = formatPriceAndVolForView(total, ValueType.Fiat, currency)
            adapter.submitList(specificWalletList)
        }

        viewModel.coinLiveData.observe(viewLifecycleOwner) {
            viewModel.walletRefresh(it)
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