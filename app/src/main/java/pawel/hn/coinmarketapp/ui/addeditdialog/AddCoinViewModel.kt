package pawel.hn.coinmarketapp.ui.addeditdialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.repository.RepositoryInterface
import javax.inject.Inject

@HiltViewModel
class AddCoinViewModel @Inject constructor (private val repository: RepositoryInterface)
    : ViewModel() {

    fun createWalletCoin(coinName: String, coinVolume: Double): Wallet {
        return repository.createWalletCoin(coinName, coinVolume)
    }

    fun addToWallet(walletCoin: Wallet) {
       viewModelScope.launch {
           repository.addToWallet(walletCoin)
       }
    }

    fun coinsNamesList(): Array<String> {
        val list = Array(repository.coinsRepository.value!!.size){
            repository.coinsRepository.value!![it].name
        }
        list.sort()

        return list
    }
}