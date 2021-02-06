package pawel.hn.coinmarketapp.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.databinding.FragmentCoinsBinding

class CoinsFragment : Fragment(R.layout.fragment_coins) {

    private val viewModel: CoinsViewModel by lazy {
        ViewModelProvider(this).get(CoinsViewModel::class.java)
    }
    private lateinit var binding: FragmentCoinsBinding
    lateinit var searchView: SearchView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCoinsBinding.bind(view)

        viewModel.coinsList.observe(viewLifecycleOwner) {
            binding.coinsRecyclerView.adapter = CoinsAdapter(it)
        }


        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_coins, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                viewModel.response()
            }
            R.id.menu_favourite -> {
                item.isChecked = !item.isChecked
            }

        }


        return super.onOptionsItemSelected(item)
    }
}