package pawel.hn.coinmarketapp.ui.coins

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import pawel.hn.coinmarketapp.CoinsApplication
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.databinding.FragmentCoinsBinding
import pawel.hn.coinmarketapp.onQueryTextChanged

class CoinsFragment : Fragment(R.layout.fragment_coins), CoinsAdapter.CoinsOnClick {

    private val viewModel: CoinsViewModel by viewModels {
        CoinsViewModel.CoinsViewModelFactory(
            (this.requireActivity().application as CoinsApplication).repository
        )
    }


    lateinit var searchView: SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val adapter = CoinsAdapter(this@CoinsFragment)
        val binding = FragmentCoinsBinding.inflate(inflater, container, false)
        binding.coinsRecyclerView.adapter = adapter


        viewModel.coinList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.eventProgressBar.observe(viewLifecycleOwner){
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
                Log.d("PHN", "menu clicked")
                viewModel.getCoinsFromDataBase(START, LIMIT, CONVERT)
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
        Log.d(TAG, "$coin")
    }
}