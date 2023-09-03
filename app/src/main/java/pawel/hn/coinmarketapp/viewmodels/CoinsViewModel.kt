package pawel.hn.coinmarketapp.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.toPresentation
import pawel.hn.coinmarketapp.usecase.GetCoinsListingsUseCase
import pawel.hn.coinmarketapp.util.showLogN
import javax.inject.Inject

const val PAGE_SIZE = 20
const val START_INDEX = 1

@HiltViewModel
class CoinsViewModel @Inject constructor(
    private val getCoinsListingsUseCase: GetCoinsListingsUseCase
) : ViewModel() {

    val coins = mutableStateListOf<CoinForView>()

    private var startIndex by mutableStateOf(1)
    var canPaginate by mutableStateOf(false)
    var listState by mutableStateOf(ListState.IDLE)

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showLogN("CoroutineExceptionHandler")
        listState = ListState.ERROR
    }

    fun getNews() = viewModelScope.launch(errorHandler) {
        val shouldLoadData =
            startIndex == START_INDEX ||
                    (startIndex != START_INDEX && canPaginate) && listState == ListState.IDLE

        if (shouldLoadData) {
            listState = if (startIndex == 1) ListState.LOADING else ListState.PAGINATING

            getCoinsListingsUseCase.executePaging(startIndex, 20)
                .catch { listState = ListState.ERROR }
                .collect { result ->
                    canPaginate = startIndex < 201
                    val list = result.map { it.toPresentation() }

                    if (startIndex == START_INDEX) {
                        coins.clear()
                        coins.addAll(list)
                    } else {
                        coins.addAll(list)
                    }

                    listState = ListState.IDLE

                    if (canPaginate) {
                        startIndex += PAGE_SIZE
                    }
                }
        }
    }

    fun resetPaging() {
        startIndex = 1
        listState = ListState.IDLE
        canPaginate = false
    }

    override fun onCleared() {
        resetPaging()
        super.onCleared()
    }

    init {
        getNews()
    }
}

enum class ListState {
    IDLE,
    LOADING,
    PAGINATING,
    ERROR,
    PAGINATION_EXHAUST,
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