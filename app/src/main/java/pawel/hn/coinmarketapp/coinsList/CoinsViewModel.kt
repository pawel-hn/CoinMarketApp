package pawel.hn.coinmarketapp.coinsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
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
import pawel.hn.coinmarketapp.util.UIState
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
        .catch { uiState.value = UIState.Error("combine") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), Pair("", false))

    private val uiState = MutableStateFlow<UIState<List<Coin>>>(UIState.Loading())
    val state: StateFlow<UIState<List<Coin>>> = uiState.asStateFlow()

    init {
        getCoins()
        update()

        viewModelScope.launch {
            delay(1000)
            coinRepository.coins
                .collectLatest {
                    uiState.value = UIState.Loaded(it)
                }
        }
    }

    fun getCoins() {
        viewModelScope.launch {
            uiState.value = UIState.Loading()
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