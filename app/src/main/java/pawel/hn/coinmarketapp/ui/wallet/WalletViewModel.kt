package pawel.hn.coinmarketapp.ui.wallet


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.formatterTotal
import pawel.hn.coinmarketapp.repository.Repository
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val walletList = repository.walletRepository
    val coinList = repository.coinsRepository

    val eventRefresh = MutableLiveData(false)

    fun calculateTotal(): String {
        val total = walletList.value!!.sumByDouble {
            it.total.replace(",", "").toDouble()
        }
        return "${formatterTotal.format(total)} USD"
    }

    fun onTaskSwiped(coin: Wallet) = viewModelScope.launch {
        repository.deleteFromWallet(coin)
    }

    fun refreshData() = viewModelScope.launch {
        eventRefresh.value = true
        repository.refreshData()
        eventRefresh.value = false
    }

     fun walletRefresh(list: List<Coin>) = viewModelScope.launch {

        val listTemp = list.filter { c ->
            c.name == walletList.value?.find { it.name == c.name }?.name
        }
        if (listTemp.isNullOrEmpty()) {
            eventRefresh.value = false
            return@launch
        }
        walletList.value!!.forEach { coin ->
            val newPrice = listTemp.filter { it.name == coin.name }[0].price
            repository.updateWallet(coin, newPrice)
        }
    }
}