package pawel.hn.coinmarketapp.ui.addeditdialog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.formatter
import pawel.hn.coinmarketapp.formatterTotal
import pawel.hn.coinmarketapp.repository.Repository
import javax.inject.Inject

@HiltViewModel
class AddCoinViewModel @Inject constructor (private val repository: Repository) : ViewModel() {

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
}