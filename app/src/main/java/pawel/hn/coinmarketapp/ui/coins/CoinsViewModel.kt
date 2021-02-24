package pawel.hn.coinmarketapp.ui.coins

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.api.Repository
import pawel.hn.coinmarketapp.database.Coin


class CoinsViewModel(private val repository: Repository) : ViewModel() {


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
        repository.coinsList(showChecked.value!!, it)
    }


    init {
        getCoinsFromDataBase()
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

    fun getCoinsFromDataBase() {
        Log.d(TAG, "getCoinsFromDataBase called")
        viewModelScope.launch {
            eventProgressBar.value = true
            repository.refreshData()
            eventProgressBar.value = false
        }
    }


    fun coinCheckedBoxClicked(coin: Coin, isChecked: Boolean) {
        viewModelScope.launch {
            repository.update(coin, isChecked)
        }
    }

    fun showChecked(isChecked: Boolean) {
        Log.d(TAG, "$isChecked")
        showChecked.value = isChecked
    }

    fun unCheckAll() {
        viewModelScope.launch {
            coinList.value?.forEach {
                repository.update(it, false)
            }
        }
    }


    class CoinsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CoinsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CoinsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class (coins)")
        }
    }
}