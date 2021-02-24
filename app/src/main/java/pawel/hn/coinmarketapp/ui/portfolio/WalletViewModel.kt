package pawel.hn.coinmarketapp.ui.portfolio


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.api.Repository
import pawel.hn.coinmarketapp.database.Wallet
import java.text.DecimalFormat

class WalletViewModel(private val repository: Repository) : ViewModel() {

    private val coins = repository.coinsRepository
    val walletList = repository.walletRepository

    fun coinsNamesList(): Array<String> {
        val list = Array(coins.value!!.size) { coins.value!![it].name }
        list.sort()
        return list
    }


    fun calculateTotal(): String {
        val total = walletList.value!!.sumByDouble {
            it.total.replace(",", "").toDouble()
        }
        val formatter = DecimalFormat("###,###")
        return "${formatter.format(total)} USD"
    }

    fun onTaskSwiped(coin: Wallet) {
        viewModelScope.launch {
            repository.deleteFromWallet(coin)
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