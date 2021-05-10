package pawel.hn.coinmarketapp.ui.wallet


import android.text.SpannableString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.util.ValueType
import pawel.hn.coinmarketapp.util.formatPriceAndVolForView
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val walletLiveData = repository.wallet.wallet
    val coinLiveData = repository.coins.coinsAll

    private val _eventProgressBar = MutableLiveData(false)
    val eventProgressBar: LiveData<Boolean>
    get() = _eventProgressBar

    private val _eventErrorResponse = MutableLiveData<Boolean>()
    val eventErrorResponse: LiveData<Boolean>
        get() = _eventErrorResponse

    fun calculateTotal(list: List<Wallet>): SpannableString {
        val total = list.sumByDouble {
            it.total
        }
        return formatPriceAndVolForView(total, ValueType.Fiat)
    }

    fun onTaskSwiped(coin: Wallet) = viewModelScope.launch {
        repository.wallet.deleteFromWallet(coin)
    }

    fun refreshData() = viewModelScope.launch {
        _eventProgressBar.value = true
        repository.refreshData()
        _eventProgressBar.value = false
        _eventErrorResponse.value = repository.responseError
    }

     fun walletRefresh(list: List<Coin>) = viewModelScope.launch {

        val listTemp = list.filter { coin ->
            coin.name == walletLiveData.value?.find { it.name == coin.name }?.name
        }
        if (listTemp.isNullOrEmpty()) {
            _eventProgressBar.value = false
            return@launch
        }
        walletLiveData.value!!.forEach { coin ->
            val newPrice = listTemp.filter { it.name == coin.name }[0].price
            repository.wallet.updateWallet(coin, newPrice)
        }
    }
}