package pawel.hn.coinmarketapp.ui.wallet


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.util.formatterTotal
import pawel.hn.coinmarketapp.repository.RepositoryInterface
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(private val repository: RepositoryInterface) : ViewModel() {

    val walletList = repository.walletRepository
    val coinList = repository.coinsRepository

    private val _eventRefresh = MutableLiveData(false)
    val eventRefresh: LiveData<Boolean>
    get() = _eventRefresh

    fun calculateTotal(list: List<Wallet>): String {
        val total = list.sumByDouble {
            it.total
        }
        return "${formatterTotal.format(total)} USD"
    }

    fun onTaskSwiped(coin: Wallet) = viewModelScope.launch {
        repository.deleteFromWallet(coin)
    }

    fun refreshData() = viewModelScope.launch {
        _eventRefresh.value = true
        repository.refreshData()
        _eventRefresh.value = false
    }

     fun walletRefresh(list: List<Coin>) = viewModelScope.launch {

        val listTemp = list.filter { coin ->
            coin.name == walletList.value?.find { it.name == coin.name }?.name
        }
        if (listTemp.isNullOrEmpty()) {
            _eventRefresh.value = false
            return@launch
        }
        walletList.value!!.forEach { coin ->
            val newPrice = listTemp.filter { it.name == coin.name }[0].price
            repository.updateWallet(coin, newPrice)
        }
    }
}