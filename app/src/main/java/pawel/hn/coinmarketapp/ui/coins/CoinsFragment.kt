package pawel.hn.coinmarketapp.ui.coins

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.databinding.FragmentCoinsBinding
import pawel.hn.coinmarketapp.util.CURRENCY_USD
import pawel.hn.coinmarketapp.util.onQueryTextChanged
import pawel.hn.coinmarketapp.util.showLog


@AndroidEntryPoint
class CoinsFragment : Fragment(R.layout.fragment_coins), CoinsAdapter.CoinsOnClick {

    private val viewModel: CoinsViewModel by viewModels()
    private lateinit var searchView: SearchView
    private lateinit var binding: FragmentCoinsBinding
    private lateinit var adapter: CoinsAdapter
    private var currency = ""




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        currency = sharedPreferences.getString(
            requireContext().getString(R.string.settings_currency_key),
            CURRENCY_USD
        ) ?: "USD"


        viewModel.refreshData(currency)
        adapter = CoinsAdapter(this@CoinsFragment)
        binding = FragmentCoinsBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = this@CoinsFragment
            coinViewModel = viewModel
            coinsRecyclerView.adapter = adapter
            coinsRecyclerView.itemAnimator = null
        }
        subscribeToObservers()
        setHasOptionsMenu(true)
        return binding.root
    }


    private fun subscribeToObservers() {
        viewModel.observableCoinList.observe(viewLifecycleOwner) { list ->
            adapter.setCurrency(currency)
            adapter.submitList(list)
        }
        viewModel.coinsTest.observe(viewLifecycleOwner) {}
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_coins, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.onQueryTextChanged {
            viewModel.searchQuery(it)
        }
        searchView.clearFocus()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                viewModel.refreshData(currency)
            }
            R.id.menu_favourite -> {
                item.isChecked = !item.isChecked
                applyStarColor(item.isChecked, item)
                viewModel.showFavourites(item.isChecked)
                binding.coinsRecyclerView.scrollToPosition(0)
            }
            R.id.menu_uncheck -> {
                viewModel.unCheckAll()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyStarColor(isChecked: Boolean, menuItem: MenuItem) {
        if(menuItem.itemId == R.id.menu_favourite) {
            if (isChecked) {
                menuItem.icon.setTint(
                    ContextCompat.getColor(requireContext(), R.color.yellow)
                )
            } else {
                menuItem.icon.setTint(
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
            }
        }

    }

    override fun onCheckBoxClicked(coin: Coin, isChecked: Boolean) {
        viewModel.coinCheckedBoxClicked(coin, isChecked)
    }

    override fun onCoinClicked(coin: Coin) {
        showLog(coin.toString())
    }
}