package pawel.hn.coinmarketapp.ui.coins

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.repository.RepositoryInterface
import pawel.hn.coinmarketapp.showLog
import javax.inject.Inject

@HiltViewModel
class CoinsViewModel @Inject constructor( val repository: RepositoryInterface) : ViewModel() {

    private val showChecked = MutableLiveData(false)
    val searchQuery = MutableLiveData("")
    val coinList = MediatorLiveData<List<Coin>>()
    val eventProgressBar = MutableLiveData(false)

    private val coinListChecked = Transformations.switchMap(showChecked) {
        if (it) {
            repository.coinListChecked
        } else {
            repository.coinsRepository
        }
    }
    private val coinListSearchQuery = Transformations.switchMap(searchQuery) {
        repository.getCoinsList(showChecked.value!!, it)
    }

    init {
        showLog("coinsViewmodel init called")
        refreshData()
        mediatorSource()
    }

    private fun mediatorSource() {
        coinList.addSource(coinListChecked) {
            coinList.value = it
        }
        coinList.addSource(coinListSearchQuery) {
            coinList.value = it
        }
    }

    fun refreshData() {
        Log.d(TAG, "getCoinsFromDataBase called")
        viewModelScope.launch {
            eventProgressBar.value = true
            repository.refreshData()
            eventProgressBar.value = false
        }
    }

    fun coinCheckedBoxClicked(coin: Coin, isChecked: Boolean) {
        viewModelScope.launch {
            repository.updateCoin(coin, isChecked)
        }
    }

    fun showChecked(isChecked: Boolean) {
        Log.d(TAG, "$isChecked")
        showChecked.value = isChecked
    }

    fun unCheckAll() {
        viewModelScope.launch {
            coinList.value?.forEach {
                repository.updateCoin(it, false)
            }
        }
    }
}