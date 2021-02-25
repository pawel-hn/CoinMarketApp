package pawel.hn.coinmarketapp.ui.wallet


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.api.Repository
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.formatter
import pawel.hn.coinmarketapp.formatterTotal

class WalletViewModel(private val repository: Repository) : ViewModel() {

    private val coins = repository.coinsRepository
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

    class WalletViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WalletViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class (portfolio)")
        }
    }
}