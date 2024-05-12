package pawel.hn.coinmarketapp.coinsList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.errorHandler
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CoinsViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val _showFavourites = MutableStateFlow(false)
    val showFavourites = _showFavourites.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _state = MutableStateFlow<Resource<List<Coin>>>(Resource.Loading())
    val state: StateFlow<Resource<List<Coin>>> = _state.asStateFlow()

    init {
        getCoins()
        observeQuery()
        observeShowFavourites()
        observeCoins(query.value, showFavourites.value)
    }

    private fun observeCoins(query: String, showFavourites: Boolean) {
        viewModelScope.launch {
            coinRepository.observeCoins(query, showFavourites)
                .collectLatest {
                    Log.d("PHN", "collect: " + query + ", fav: " + showFavourites)
                    _state.value = Resource.Success(it)
                }
        }
    }

    private fun observeQuery() {
        viewModelScope.launch {
           _query.debounce(300).collectLatest {
               observeCoins(it, _showFavourites.value)
           }
        }
    }

    private fun observeShowFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            _showFavourites.debounce(300).collect {
                observeCoins(_query.value, it)
            }
        }
    }

    fun getCoins() {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            coinRepository.getCoinsPagingFromApi()
        }
    }
    fun favouriteClick(id: Int, isFavourite: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        if (isFavourite) {
            coinRepository.saveFavouriteCoinId(id)
        } else {
            coinRepository.deleteFavouriteCoinId(id)
        }
    }

    fun showFavouritesClick(onlyFavourites: Boolean) {
        _showFavourites.value = onlyFavourites
    }

    fun queryChange(query: String) {
        _query.value = query
    }
}