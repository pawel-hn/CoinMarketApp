package pawel.hn.coinmarketapp.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import androidx.room.PrimaryKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.toPresentation
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.usecase.GetCoinsListingsUseCase
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.showLogN
import java.net.HttpRetryException
import javax.inject.Inject

@HiltViewModel
class CoinsViewModel @Inject constructor(
    private val repository: Repository,
    private val getCoinsListingsUseCase: GetCoinsListingsUseCase,
) : ViewModel() {

    private val showFavourites = MutableLiveData(false)

    private val searchQuery = MutableLiveData("")

    val observableCoinsAllMediator = MediatorLiveData<List<Coin>>()

    val observableCoinsAll = repository.coins.coinsAll

    private val _eventErrorResponse = MutableLiveData<Boolean>()
    val eventErrorResponse: LiveData<Boolean> = _eventErrorResponse

    private val _coinResult = MutableStateFlow<Resource<List<CoinForView>>>(Resource.Loading())
    val coinResult: StateFlow<Resource<List<CoinForView>>> = _coinResult.asStateFlow()

    private val _eventProgressBar = MutableLiveData(false)
    val eventProgressBar: LiveData<Boolean> = _eventProgressBar

    private val coinListChecked = showFavourites.switchMap {
        if (it) {
            repository.coins.coinsFavourite
        } else {
            repository.coins.coinsAll
        }
    }

    /**
     * UDAPTE
     */

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showLogN("CoroutineExceptionHandler")
        val data = Resource.Error<List<CoinForView>>(throwable.message ?: "Unknown error")
        _coinResult.value = data
    }

     fun getCoins() {
        _coinResult.value = Resource.Loading()
        viewModelScope.launch(context = Dispatchers.IO + errorHandler) {
            val data = getCoinsListingsUseCase.execute()

            _coinResult.value = Resource.Success(
                data.map {coin -> coin.toPresentation()}
            )
        }
    }

    /**
     *  // UPDATE
     */

    private val coinListSearchQuery = searchQuery.switchMap { searchQuery ->
        repository.coins.getCoinsList(searchQuery, showFavourites.value!!)
    }

    init {
        getCoins()
      //  mediatorSource()
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

data class CoinForView(
    val coinId: Int,
    val name: String,
    val symbol: String,
    val favourite: Boolean,
    val price: String,
    val change24h: String,
    val isChange24hUp: Boolean,
    val change7d: String,
    val isChange7dUp: Boolean,
    val cmcRank: Int,
    val imageUri: Uri
)