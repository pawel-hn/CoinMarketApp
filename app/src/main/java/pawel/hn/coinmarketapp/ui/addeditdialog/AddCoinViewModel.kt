package pawel.hn.coinmarketapp.ui.addeditdialog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.formatter
import pawel.hn.coinmarketapp.formatterTotal

class AddCoinViewModel(private val repository: Repository) : ViewModel() {

    private val coins = repository.coinsRepository

    fun addToWallet(coinName: String, coinVolume: String) {

        val price = coins.value?.find {it.name == coinName}?.price ?: 0
        val total = formatterTotal.format(price.toDouble() * coinVolume.toDouble())
        val priceFormat = formatter.format(price)
        val coin = Wallet(coinName,formatter.format(coinVolume.toDouble()),priceFormat, total)
        Log.d(TAG, coin.toString())
        viewModelScope.launch {
            repository.insertIntoWallet(coin)
        }
    }

    fun coinsNamesList(): Array<String> {
        val list = Array(coins.value!!.size){coins.value!![it].name}
        list.sort()
        return list
    }

    class AddCoinViwModelFactory(private val repository: Repository) : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddCoinViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddCoinViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown AddCoinViewModel class (portfolio)")
        }
    }


}