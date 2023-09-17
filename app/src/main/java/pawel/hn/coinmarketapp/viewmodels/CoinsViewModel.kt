package pawel.hn.coinmarketapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.showLogN
import javax.inject.Inject


@HiltViewModel
class CoinsViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val coins = MutableStateFlow<Resource<List<Coin>>>(Resource.Loading())
    private val favouriteIds = MutableStateFlow<List<Int>>(emptyList())
    private val showFavourites = MutableStateFlow(false)

    val coinResult: StateFlow<Resource<List<Coin>>> = combine(
        coins,
        favouriteIds,
        showFavourites
    ) { coins, ids, showFavourites ->
        when (coins) {
            is Resource.Error -> coins
            is Resource.Loading -> coins
            is Resource.Success -> {
                val data = (coins.data ?: emptyList()).map { coin ->
                    coin.copy(favourite = ids.any { it == coin.coinId })
                }
                val forView = if (showFavourites)
                    data.filter { it.favourite } else data
                Resource.Success(forView)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading()
    )

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showLogN("CoroutineExceptionHandler")
        coins.value = Resource.Error("corotuine error")
    }

    init {
        getCoins()
    }

    fun getCoins() =
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            coins.value = Resource.Loading()
            coinRepository.getCoinsPagingFromApi().runCatching {
                observeCoins("")
                observeFavourites()
            }.onFailure {
                coins.value = Resource.Error("corotuine error")
            }
        }

    private fun observeCoins(query: String) =
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            coinRepository.getCoinsFromDatabase(query).collect {
                coins.value = Resource.Success(it)
            }
        }

    private fun observeFavourites() =
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            coinRepository.getFavourites().collect {
                favouriteIds.value = it
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