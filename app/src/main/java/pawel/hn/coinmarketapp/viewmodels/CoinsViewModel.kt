package pawel.hn.coinmarketapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.repository.Repository
import javax.inject.Inject

@HiltViewModel
class CoinsViewModel @Inject constructor(
    val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val showFavourites = MutableLiveData(false)

    private val searchQuery = MutableLiveData("")

    val observableCoinsAllMediator = MediatorLiveData<List<Coin>>()

    val observableCoinsAll = repository.coins.coinsAll

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
        repository.coins.getCoinsList(searchQuery, showFavourites.value!!)
    }

    init {
        mediatorSource()
    }

    private fun mediatorSource() {
        observableCoinsAllMediator.addSource(coinListChecked) {
            observableCoinsAllMediator.postValue(it)
        }
        observableCoinsAllMediator.addSource(coinListSearchQuery) {
            observableCoinsAllMediator.postValue(it)
        }
    }

    fun refreshData(ccy: String) {
        viewModelScope.launch {
            _eventProgressBar.value = true
            repository.getCoinsData(ccy)
            _eventProgressBar.value = false
            _eventErrorResponse.value = repository.responseError

        }
    }


    fun coinFavouriteClicked(coin: Coin, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.coins.update(coin, isChecked)
        }
    }

    fun unCheckAllFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            observableCoinsAllMediator.value?.forEach {
                repository.coins.update(it, false)
            }
        }
    }

    fun searchQuery(query: String) {
        searchQuery.value = query
    }

    fun showFavourites(showFav: Boolean) = showFavourites.postValue(showFav)

}

