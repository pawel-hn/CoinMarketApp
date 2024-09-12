package pawel.hn.coinmarketapp.coinsList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.util.UIState
import javax.inject.Inject


@HiltViewModel
class CoinsViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val _showFavourites = MutableStateFlow(false)
    val showFavourites = _showFavourites.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val search = combine(
        _query,
        _showFavourites
    ) { query, favourites ->
        Pair(query, favourites)
    }
        .catch { uiState.value = UIState.Error("combine") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), Pair("", false))

    private val uiState = MutableStateFlow<UIState<List<Coin>>>(UIState.Loading())
    val state: StateFlow<UIState<List<Coin>>> = uiState.asStateFlow()

    init {
        getCoins()
        observe()

        viewModelScope.launch {
            delay(1000)
            coinRepository.coins
                .collectLatest {
                    Log.d("PHN", "viewModel collectLatest init")
                    uiState.value = if (it.isLoading) {
                        UIState.Loading()
                    } else {
                        UIState.Loaded(it.coins)
                    }
                }
        }
    }

    fun getCoins() {
        viewModelScope.launch {
            coinRepository.getCoinsPagingFromApi()
        }
    }

    private fun observe() =
        viewModelScope.launch {
            search.collectLatest {
                coinRepository.observeCoins(it.first, it.second)
            }
        }


    fun favouriteClick(id: Int, isFavourite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isFavourite) {
                coinRepository.saveFavouriteCoinId(id)
            } else {
                coinRepository.deleteFavouriteCoinId(id)
            }
        }
    }

    fun showFavouritesClick(onlyFavourites: Boolean) {
        _showFavourites.value = onlyFavourites
    }

    fun queryChange(query: String) {
        _query.value = query
    }
}