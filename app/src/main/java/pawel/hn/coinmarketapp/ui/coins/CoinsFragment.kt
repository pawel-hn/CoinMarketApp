package pawel.hn.coinmarketapp.ui.coins

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.databinding.FragmentCoinsBinding
import pawel.hn.coinmarketapp.onQueryTextChanged
import pawel.hn.coinmarketapp.showLog


@AndroidEntryPoint
class CoinsFragment : Fragment(R.layout.fragment_coins), CoinsAdapter.CoinsOnClick {

    private val viewModel: CoinsViewModel by viewModels()

    private lateinit var searchView: SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val adapter = CoinsAdapter(this@CoinsFragment)
        val binding = FragmentCoinsBinding.inflate(inflater, container, false)
        binding.apply {
            coinsRecyclerView.adapter = adapter
            coinsRecyclerView.itemAnimator = null
        }
        viewModel.coinList.observe(viewLifecycleOwner) {
            showLog("coins observer")
            adapter.submitList(it)
        }
        viewModel.eventProgressBar.observe(viewLifecycleOwner) {
            if (it) {
                binding.apply {
                    coinsRecyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    coinsRecyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_coins, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
        searchView.clearFocus()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                viewModel.refreshData()
            }
            R.id.menu_favourite -> {
                item.isChecked = !item.isChecked
                viewModel.showChecked(item.isChecked)
            }
            R.id.menu_uncheck -> {
                viewModel.unCheckAll()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCheckBoxClicked(coin: Coin, isChecked: Boolean) {
        viewModel.coinCheckedBoxClicked(coin, isChecked)
    }

    override fun onCoinClicked(coin: Coin) {
    }
}