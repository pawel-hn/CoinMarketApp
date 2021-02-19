package pawel.hn.coinmarketapp.ui.portfolio

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pawel.hn.coinmarketapp.api.Repository

class WalletViewModel(private val repository: Repository) : ViewModel() {

    val coins = repository.coinList


    fun coinsNamesList(): Array<String> {
        val list = Array<String>(coins.value!!.size){coins.value!![it].name}
        list.sort()
        return list
    }

    fun showDialog(context: Context) {

    }

    class WalletViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
           if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
               return WalletViewModel(repository) as T
           }
            throw IllegalArgumentException("Unknown ViewModel class (portfolio)")
        }
    }
}