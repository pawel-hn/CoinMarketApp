package pawel.hn.coinmarketapp.coinsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    private val _state = MutableStateFlow<Resource<List<Coin>>>(Resource.Loading())
    private val showFavourites = MutableStateFlow(false)


    val state: StateFlow<Resource<List<Coin>>> = combine(
        _state,
        showFavourites
    ) { currentState, showFavourites ->
        when (currentState) {
            is Resource.Error, is Resource.Loading -> currentState
            is Resource.Success -> {
                val coins = if (showFavourites)
                    currentState.data?.filter { it.favourite } else
                    currentState.data

                Resource.Success(coins ?: emptyList())
            }
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), Resource.Loading())


    init {
        getCoins()
    }

    fun observeCoins(query: String) = viewModelScope.launch(Dispatchers.IO + errorHandler) {
        coinRepository.observeCoins(query).collect {
            _state.value = Resource.Success(it)
        }
    }

    fun getCoins() =
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            _state.value = Resource.Loading()
            coinRepository.getCoinsPagingFromApi().runCatching {
                observeCoins("")
            }
        }

    fun favouriteClick(id: Int, isFavourite: Boolean) = viewModelScope.launch {
        if (isFavourite) {
            coinRepository.saveFavouriteCoinId(id)
        } else {
            coinRepository.deleteFavouriteCoinId(id)
        }
    }

    fun showFavouritesClick(onlyFavourites: Boolean) {
        showFavourites.value = onlyFavourites
    }
}