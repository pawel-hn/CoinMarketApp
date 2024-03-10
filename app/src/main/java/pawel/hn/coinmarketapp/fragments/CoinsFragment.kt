package pawel.hn.coinmarketapp.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.adapters.CoinsAdapter
import pawel.hn.coinmarketapp.databinding.FragmentCoinsBinding
import pawel.hn.coinmarketapp.util.*
import pawel.hn.coinmarketapp.viewmodels.CoinsViewModel


@AndroidEntryPoint
class CoinsFragment : Fragment(R.layout.fragment_coins) {
// test / test //cc
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


       // viewModel.refreshData(currency)
        adapter = CoinsAdapter { coin, isChecked ->
            viewModel.coinFavouriteClicked(coin, isChecked)
        }
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
        viewModel.observableCoinsAllMediator.observe(viewLifecycleOwner) { list ->

            adapter.setCurrency(currency)
            adapter.submitList(list)
        }
        viewModel.observableCoinsAll.observe(viewLifecycleOwner) {}

        lifecycleScope.launch {
            viewModel.coinResult.collectLatest {
                when(it) {
                    is Resource.Error -> showLogN("error: ${it.message}")
                    is Resource.Loading -> showLogN("loading")
                    is Resource.Success -> showLogN("success: ${it.data?.size}")
                }
            }
        }

    }

}