package pawel.hn.coinmarketapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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

    private val showFavourites = MutableStateFlow(false)

    val coinResult: Flow<Resource<List<Coin>>> = combine(
        coins,
        showFavourites
    ) { coins, showFavourites ->
        when (coins) {
            is Resource.Error -> coins
            is Resource.Loading -> coins
            is Resource.Success -> {
                val data = coins.data ?: emptyList()
                val forView = if (showFavourites)
                    data.filter { it.favourite } else data
                Resource.Success(forView)
            }
        }
    }

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showLogN("CoroutineExceptionHandler")
        coins.value = Resource.Error("corotuine error")
    }

    init {
        getCoins()
    }

    private fun observeCoins() = viewModelScope.launch(Dispatchers.IO + errorHandler) {
        coinRepository.observeCoins().collect {
            coins.value = Resource.Success(it)
        }
    }

    fun getCoins() =
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            coins.value = Resource.Loading()
            coinRepository.getCoinsPagingFromApi().runCatching {
                observeCoins()
            }.onFailure {
                coins.value = Resource.Error("corotuine error")
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