package pawel.hn.coinmarketapp.ui.wallet


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.repository.Repository
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

    fun calculateTotal(list: List<Wallet>): Double = list.sumByDouble { it.total }

    fun totalWallet(list: List<Wallet>):  List<Wallet>{

        val totalList = mutableListOf<Wallet>()
        val listOfCoinIds = mutableListOf<Int>()

        for (coinLoop in list) {
            if (listOfCoinIds.contains(coinLoop.coinId)) {
                continue
            } else {
                listOfCoinIds.add(coinLoop.coinId)
                val tempList = list.filter { it.coinId == coinLoop.coinId }
                val newVolume = tempList.sumByDouble { it.volume}
                val newTotal = tempList.sumByDouble { it.total }
                totalList.add(
                    Wallet(coinId = coinLoop.coinId, name = coinLoop.name,
                        volume = newVolume, price = coinLoop.price, total = newTotal, walletNo = 3)
                )
            }
        }

        return totalList.sortedByDescending { it.total }
    }


    fun onTaskSwiped(coin: Wallet) = viewModelScope.launch {
        repository.wallet.deleteFromWallet(coin)
    }

    fun refreshData(ccy: String) = viewModelScope.launch {
        _eventProgressBar.value = true
        repository.refreshData(ccy)
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

    fun deleteAll() {
        viewModelScope.launch {
            repository.wallet.deleteAllFromWallets()
        }
    }
}