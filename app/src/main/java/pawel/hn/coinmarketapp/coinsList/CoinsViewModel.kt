package pawel.hn.coinmarketapp.coinsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
import javax.inject.Inject


@HiltViewModel
class CoinsViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val _showFavourites = MutableStateFlow(false)
    val showFavourites = _showFavourites.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val search = combine(
        _query,
        _showFavourites
    ) { query, search ->
        Pair(query, search)
    }
        .catch { _state.value = Resource.Error("combine") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), Pair("", false))

    private val _state = MutableStateFlow<Resource<List<Coin>>>(Resource.Loading())
    val state: StateFlow<Resource<List<Coin>>> = _state.asStateFlow()

    init {
        getCoins()
        update()

        viewModelScope.launch {
            coinRepository.coins
                .collectLatest {
                _state.value = Resource.Success(it)
            }
        }
    }

    fun getCoins() {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            coinRepository.getCoinsPagingFromApi()
        }
    }

    @OptIn(FlowPreview::class)
    fun update() =
        viewModelScope.launch {
            search.debounce(300).collectLatest {
                coinRepository.observeCoins(it.first, it.second)
            }
        }


    fun favouriteClick(id: Int, isFavourite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isFavourite) {
                coinRepository.saveFavouriteCoinId(id)
            } else {
                coinRepository.deleteFavouriteCoinId(id)
            }
        }
    }

    fun showFavouritesClick(onlyFavourites: Boolean) {
        _showFavourites.value = onlyFavourites
    }

    fun queryChange(query: String) {
        _query.value = query
    }
}