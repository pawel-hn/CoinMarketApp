package pawel.hn.coinmarketapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.repository.Repository
import javax.inject.Inject

@HiltViewModel
class AddCoinViewModel @Inject constructor (private val repository: Repository)
    : ViewModel() {

    val coins = repository.coins.coinsAll

    /**
     * Passing request for creating new item which can be inserted into Wallet.
     */
    fun createWalletCoin(coinName: String, coinVolume: Double, walletNo: Int): Wallet {
        return repository.wallet.createWalletCoin(coinName, coinVolume, walletNo, coins.value!!)
    }

    /**
     * After Wallet object is created,this method puts it into database.
     */
    fun addToWallet(walletCoin: Wallet) {
       viewModelScope.launch {
           repository.wallet.addToWallet(walletCoin)
       }
    }

    /**
     * Prepares list of names of Coins which can be added to Wallet.
     * Presented in spinner through dialog, sorted alphabetically.
     */
    fun coinsNamesList(): Array<String> {
        val list = Array(repository.coins.coinsAll.value!!.size){
            repository.coins.coinsAll.value!![it].name
        }
        list.sort()
        return list
    }
}