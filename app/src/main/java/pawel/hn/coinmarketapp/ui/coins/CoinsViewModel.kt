package pawel.hn.coinmarketapp.ui.coins

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.repository.RepositoryInterface
import javax.inject.Inject

@HiltViewModel
class CoinsViewModel @Inject constructor(val repository: RepositoryInterface) : ViewModel() {

    private val showChecked = MutableLiveData(false)
    private val searchQuery = MutableLiveData("")

    val observableCoinList = MediatorLiveData<List<Coin>>()

    private val _eventErrorResponse = MutableLiveData<Boolean>()
    val eventErrorResponse: LiveData<Boolean>
        get() = _eventErrorResponse


    private val _eventProgressBar = MutableLiveData(false)
    val eventProgressBar: LiveData<Boolean>
        get() = _eventProgressBar


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
      //  refreshData()
      //  mediatorSource()
    }


    private fun mediatorSource() {
        observableCoinList.addSource(coinListChecked) {
            observableCoinList.value = it
        }
        observableCoinList.addSource(coinListSearchQuery) {
            observableCoinList.value = it
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _eventProgressBar.value = true
            repository.refreshData()
            _eventProgressBar.value = false
            _eventErrorResponse.value = repository.responseError
        }
    }

    fun coinCheckedBoxClicked(coin: Coin, isChecked: Boolean) {
        viewModelScope.launch {
            repository.updateCoin(coin, isChecked)
        }
    }

    fun showChecked(isChecked: Boolean) {
        showChecked.value = isChecked
    }

    fun unCheckAll() {
        viewModelScope.launch {
            observableCoinList.value?.forEach {
                repository.updateCoin(it, false)
            }
        }
    }

    fun searchQuery(query: String) {
        searchQuery.value = query
    }
}