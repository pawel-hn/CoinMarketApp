package pawel.hn.coinmarketapp.ui.wallet


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.formatter
import pawel.hn.coinmarketapp.formatterTotal
import pawel.hn.coinmarketapp.repository.Repository
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor (private val repository: Repository) : ViewModel() {

    val walletList = repository.walletRepository
    val eventRefresh = MutableLiveData(false)

    fun calculateTotal(): String {
        val total = walletList.value!!.sumByDouble {
            it.total.replace(",","").toDouble()
        }
        return "${formatterTotal.format(total)} USD"
    }

    fun onTaskSwiped(coin: Wallet) {
        viewModelScope.launch {
            repository.deleteFromWallet(coin)
        }
    }

    fun walletRefresh() {
        viewModelScope.launch {
            eventRefresh.value = true
            repository.refreshData()
            walletList.value?.forEach { coinWallet ->
                val newPrice = repository.coinsRepository.value!!.filter {it.name == coinWallet.name }[0].price
                val newTotal =
                    coinWallet.volume.replace(",","").toDouble() * newPrice

                val coin = Wallet(coinWallet.name,
                    coinWallet.volume,
                    formatter.format(newPrice),
                    formatterTotal.format(newTotal))

                repository.updateWallet(coin)
            }
            eventRefresh.value = false
        }
    }
}