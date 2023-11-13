package pawel.hn.coinmarketapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.showLogN
import javax.inject.Inject

@HiltViewModel
class AddCoinViewModel @Inject constructor (
    private val repository: Repository,
    private val coinRepository: CoinRepository
    )
    : ViewModel() {

    private val _coinList = MutableStateFlow<List<Coin>>(emptyList())
    val coinList: StateFlow<Resource<List<String>>> = _coinList
        .map { coins ->
            val names = coins.map { it.name }
            Resource.Loading<List<String>>()
        }
        .catch { Resource.Error<List<Coin>>(it.message ?: "some Error") }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(2000), Resource.Loading()
        )

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showLogN("CoroutineExceptionHandler")
    }

    init {
        getCoinsList("")
    }

    fun getCoinsList(search: String) {
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            coinRepository.observeCoins(search).collectLatest {
                _coinList.value = it
            }
        }
    }

    fun createWalletCoin(coinName: String, coinVolume: Double, walletNo: Int): Wallet {
        return repository.wallet.createWalletCoin(coinName, coinVolume, walletNo, emptyList())
    }


    fun addToWallet(walletCoin: Wallet) {
       viewModelScope.launch {
           repository.wallet.addToWallet(walletCoin)
       }
    }

    fun coinsNamesList(): Array<String> {

        return arrayOf("a","b")
    }
}