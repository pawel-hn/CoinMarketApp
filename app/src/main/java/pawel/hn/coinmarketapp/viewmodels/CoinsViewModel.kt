package pawel.hn.coinmarketapp.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.repository.CoinsRepository
import javax.inject.Inject

@HiltViewModel
class CoinsViewModel @Inject constructor(
    val coinsRepository: CoinsRepository,
) : BaseViewModel(coinsRepository) {

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

