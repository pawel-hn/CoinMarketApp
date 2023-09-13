package pawel.hn.coinmarketapp.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    val coins = mutableStateListOf<Coin>()


    private val _coinResult = MutableStateFlow<Resource<List<Coin>>>(Resource.Loading())
    val coinResult: StateFlow<Resource<List<Coin>>> = _coinResult.asStateFlow()


    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showLogN("CoroutineExceptionHandler")
        _coinResult.value = Resource.Error("corotuine error")
    }

    init {
        getCoins()
    }

    private fun getCoins() =
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            coinRepository.getCoinsPagingFromApi().runCatching {
                observeCoins("")
            }.onFailure {
                _coinResult.value = Resource.Error("corotuine error")
            }
        }

    fun observeCoins(query: String) =
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            coinRepository.getCoinsFromDatabase(query).collectLatest {
                _coinResult.value = Resource.Success(it)
            }
        }

    fun getNews() = viewModelScope.launch(errorHandler) {
        coinRepository.getCoinsFromDatabase("")
    }

    fun favouriteClick(id: Int, isFavourite: Boolean) = viewModelScope.launch {
        if (isFavourite) {
            coinRepository.deleteFavouriteCoinId(id)
        } else {
            coinRepository.saveFavouriteCoinId(id)
        }
    }

    fun resetPaging() {

    }

    override fun onCleared() {
        resetPaging()
        super.onCleared()
    }
}


fun CoroutineScope.launchWithSuccessAndError(
    onError: (Throwable) -> Unit,
    onSuccess: () -> Unit = {}
) = run {

}