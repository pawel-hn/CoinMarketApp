package pawel.hn.coinmarketapp.coinsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.errorHandler
import javax.inject.Inject


@HiltViewModel
class CoinsViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    private val _showFavourites = MutableStateFlow(false)
    val showFavourites = _showFavourites.asStateFlow()
    private val _query = MutableStateFlow<String>("")


    private val _state1 = MutableStateFlow<Resource<List<Coin>>>(Resource.Loading())
    val state1: StateFlow<Resource<List<Coin>>> = _coins.map {
        Resource.Success(it)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), Resource.Loading())


    init {
        getCoins()

        combine(
            _query,
            _showFavourites,
        ) { query, showFavourites ->
            observeCoins(query, showFavourites)
        }
            .distinctUntilChanged()
            .catch { Resource.Error<List<Coin>>("query???") }

    }

    fun observeCoins(query: String, showFavourites: Boolean) = viewModelScope.launch {
        coinRepository.observeCoins(query, showFavourites).collect {
            _coins.value = it
        }
    }

    fun getCoins() =
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            _state1.value = Resource.Loading()
            coinRepository.getCoinsPagingFromApi()
        }

    fun favouriteClick(id: Int, isFavourite: Boolean) = viewModelScope.launch {
        if (isFavourite) {
            coinRepository.saveFavouriteCoinId(id)
        } else {
            coinRepository.deleteFavouriteCoinId(id)
        }
    }

    fun showFavouritesClick(onlyFavourites: Boolean) {
        _showFavourites.value = onlyFavourites
    }
}