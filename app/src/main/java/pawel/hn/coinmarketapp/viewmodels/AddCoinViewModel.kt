package pawel.hn.coinmarketapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.domain.toWalletDatabase
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.repository.WalletRepository
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.errorHandler
import javax.inject.Inject

@HiltViewModel
class AddCoinViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val _coinList = MutableStateFlow<List<Coin>>(emptyList())
    val coinList: StateFlow<Resource<List<Coin>>> = _coinList
        .map { coins -> Resource.Success(coins) }
        .catch { Resource.Error<List<Coin>>(it.message ?: "some Error") }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(2000), Resource.Loading()
        )

    private val _isAddButtonEnabled = MutableStateFlow(false)
    val isAddButtonEnabled = _isAddButtonEnabled.asStateFlow()

    private var selectedCoin: Coin? = null
    private var amount: Double = 0.0

    init {
        observeCoins("")
    }

    fun observeCoins(search: String) {
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            coinRepository.observeCoins(search).collectLatest {
                _coinList.value = it
            }
        }
    }

    fun selectedCoin(coin: Coin) {
        selectedCoin = coin
        validateAddButton()
    }

    fun inputAmount(input: String) {
        runCatching { input.toDouble() }
            .onSuccess {
                amount = it
                validateAddButton()
            }
            .onFailure {
                it.printStackTrace()
            }
    }

    private fun validateAddButton() {
        _isAddButtonEnabled.value = selectedCoin != null && amount > 0
    }

    fun addToWallet() {
        val coin = selectedCoin ?: return

        viewModelScope.launch {
           walletRepository.saveCoinToWallet(coin, amount)
        }
    }

    fun coinsNamesList(): Array<String> {
        // do wywalenia
        return arrayOf("a", "b")
    }
}