package pawel.hn.coinmarketapp.ui.addeditdialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.repository.RepositoryInterface
import javax.inject.Inject

@HiltViewModel
class AddCoinViewModel @Inject constructor (private val repository: RepositoryInterface) : ViewModel() {

    private val coins = repository.coinsRepository

    fun addToWallet(coinName: String, coinVolume: String) {
       viewModelScope.launch {
           repository.addToWallet(coinName, coinVolume)
       }
    }

    fun coinsNamesList(): Array<String> {
        val list = Array(coins.value!!.size){coins.value!![it].name}
        list.sort()

        return list
    }
}