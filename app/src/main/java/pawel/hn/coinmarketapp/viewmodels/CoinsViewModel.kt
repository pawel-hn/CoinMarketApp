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

    /**
     * Livedata which checks if show favourites is currently checked by user.
     */
    private val showFavourites = MutableLiveData(false)

    /**
     * Livedata which checks current search query entered by user and filtering the list.
     */
    private val searchQuery = MutableLiveData("")

    /**
     * Mediator livedata, connects livedata with list of favourite coins,
     * and livedata with coins
     */
    val observableCoinsAllMediator = MediatorLiveData<List<Coin>>()

    /**
     *livedata with list of all coins, observed in CoinsFragment with no action, observed so
     * when refreshing data, database can be checked if there are already coins
     */
    val observableCoinsAll = repository.coins.coinsAll

    /**
     * Error livedata, observed in xml layout through data binding,
     * trigger by response error in Repository,
     */
    private val _eventErrorResponse = MutableLiveData<Boolean>()
    val eventErrorResponse: LiveData<Boolean>
        get() = _eventErrorResponse

    /**
     * Progress bar livedata, observed in xml layout through data binding.
     */
    private val _eventProgressBar = MutableLiveData(false)
    val eventProgressBar: LiveData<Boolean>
        get() = _eventProgressBar

    /**
     * Livedata of list of all coins or favourites, if marked by user.
     */
    private val coinListChecked = Transformations.switchMap(showFavourites) {
        if (it) {
            repository.coins.coinsFavourite
        } else {
            repository.coins.coinsAll
        }
    }

    /**
     * Livedata of list of coins filtered with current query entered by user.
     */
    private val coinListSearchQuery = Transformations.switchMap(searchQuery) { searchQuery ->
        repository.coins.getCoinsList(searchQuery, showFavourites.value!!)
    }

    init {
        mediatorSource()
    }

    /**
     * Function which adds both coins livedata (one base on query, second on favourites)
     * as sources of main MediatorLiveData
     */
    private fun mediatorSource() {
        observableCoinsAllMediator.addSource(coinListChecked) {
            observableCoinsAllMediator.postValue(it)
        }
        observableCoinsAllMediator.addSource(coinListSearchQuery) {
            observableCoinsAllMediator.postValue(it)
        }
    }

    /**
     * Requests api call and refreshes data
     */
    fun refreshData(ccy: String) {
        viewModelScope.launch {
            _eventProgressBar.value = true
            repository.getCoinsData(ccy)
            _eventProgressBar.value = false
            _eventErrorResponse.value = repository.responseError

        }
    }

    /**
     * Marked coin as favourite when clicked star on the left
     */
    fun coinFavouriteClicked(coin: Coin, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.coins.update(coin, isChecked)
        }
    }

    /**
     * Makes all coins as non-favourite
     */
    fun unCheckAllFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            observableCoinsAllMediator.value?.forEach {
                repository.coins.update(it, false)
            }
        }
    }

    /**
     * Updates livedata search query value with current input from user.
     */
    fun searchQuery(query: String) {
        searchQuery.value = query
    }

    fun showFavourites(showFav: Boolean) = showFavourites.postValue(showFav)

}

