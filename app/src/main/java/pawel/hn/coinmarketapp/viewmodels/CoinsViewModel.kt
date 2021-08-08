package pawel.hn.coinmarketapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.repository.CoinsRepository
import pawel.hn.coinmarketapp.util.toMutableLiveData
import javax.inject.Inject

@HiltViewModel
class CoinsViewModel @Inject constructor(
    val coinsRepository: CoinsRepository,
    application: Application
) : AndroidViewModel(application) {


    val eventErrorResponse: LiveData<Boolean> = MutableLiveData(false)
    val eventProgressBar: LiveData<Boolean> = MutableLiveData(false)

    private val showFavourites = MutableLiveData(false)
    private val searchQuery = MutableLiveData("")

    val allCoinsMediator = MediatorLiveData<List<Coin>>()
    val observableCoinsAll = coinsRepository.coins.coinsAll

    private val coinListChecked = Transformations.switchMap(showFavourites) {
        if (it) {
            coinsRepository.coins.coinsFavourite
        } else {
            coinsRepository.coins.coinsAll
        }
    }

    private val coinListSearchQuery = Transformations.switchMap(searchQuery) { searchQuery ->
        coinsRepository.coins.getCoinsList(searchQuery, showFavourites.value!!)
    }

    init {
        addAllCoinsSources()
    }

    private fun addAllCoinsSources() {
        allCoinsMediator.apply {
            addSource(coinListChecked) { allCoinsMediator.postValue(it) }
            addSource(coinListSearchQuery) { allCoinsMediator.postValue(it) }
        }
    }

    fun refreshData(currency: String) {
        eventProgressBar.toMutableLiveData().value = true

        viewModelScope.launch(Dispatchers.IO) {
            coinsRepository.getCoinsData(currency)
            withContext(Dispatchers.Main){
                eventProgressBar.toMutableLiveData().value = false
                eventErrorResponse.toMutableLiveData().value = coinsRepository.responseError
            }
        }
    }

    fun coinFavouriteClicked(coin: Coin, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            coinsRepository.coins.update(coin, isChecked)
        }
    }

    fun unCheckAllFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            allCoinsMediator.value?.forEach {
                coinsRepository.coins.update(it, false)
            }
        }
    }

    fun searchQuery(query: String) {
        searchQuery.value = query
    }

    fun showFavourites(showFav: Boolean) {
        showFavourites.postValue(showFav)
    }

}

