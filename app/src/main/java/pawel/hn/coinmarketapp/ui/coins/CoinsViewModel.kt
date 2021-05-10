package pawel.hn.coinmarketapp.ui.coins

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.util.showLog
import javax.inject.Inject

@HiltViewModel
class CoinsViewModel @Inject constructor(
    val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val showFavourites = MutableLiveData<Boolean>()
    private val searchQuery = MutableLiveData("")

    val observableCoinList = MediatorLiveData<List<Coin>>()
    val coinsTest = repository.coins.coinsAll

    private val _eventErrorResponse = MutableLiveData<Boolean>()
    val eventErrorResponse: LiveData<Boolean>
        get() = _eventErrorResponse


    private val _eventProgressBar = MutableLiveData(false)
    val eventProgressBar: LiveData<Boolean>
        get() = _eventProgressBar

    private val coinListChecked = Transformations.switchMap(showFavourites) {
        if (it) {
            repository.coins.coinsFavourite
        } else {
            repository.coins.coinsAll
        }
    }

    private val coinListSearchQuery = Transformations.switchMap(searchQuery) { searchQuery ->
        repository.coins.getCoinsList(searchQuery)
    }

    init {
        refreshData()
        mediatorSource()
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
            showLog("ResponseError: ${repository.responseError}")
            _eventErrorResponse.value = repository.responseError

        }
    }



    fun coinCheckedBoxClicked(coin: Coin, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.coins.update(coin, isChecked)
        }
    }


    fun unCheckAll() {
        viewModelScope.launch(Dispatchers.IO) {
            observableCoinList.value?.forEach {
                repository.coins.update(it, false)
            }
        }
    }

    fun searchQuery(query: String) {
        searchQuery.value = query
    }

    fun showFavourites(showFav: Boolean) = showFavourites.postValue(showFav)
    
}

