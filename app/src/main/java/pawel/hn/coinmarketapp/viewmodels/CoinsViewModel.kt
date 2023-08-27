package pawel.hn.coinmarketapp.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.toPresentation
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.usecase.GetCoinsListingsUseCase
import pawel.hn.coinmarketapp.util.showLogN
import javax.inject.Inject

@HiltViewModel
class CoinsViewModel @Inject constructor(
    private val repository: Repository,
    private val getCoinsListingsUseCase: GetCoinsListingsUseCase,
) : ViewModel() {

    val coins = mutableStateListOf<CoinForView>()

     var startIndex by mutableStateOf(1)
    var canPaginate by mutableStateOf(false)
    var listState by mutableStateOf(ListState.IDLE)

    private val _eventErrorResponse = MutableLiveData<Boolean>()
    val eventErrorResponse: LiveData<Boolean> = _eventErrorResponse

    private val _eventProgressBar = MutableLiveData(false)
    val eventProgressBar: LiveData<Boolean> = _eventProgressBar

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showLogN("CoroutineExceptionHandler")
        listState = ListState.ERROR
    }

//    fun getNoPaging() = viewModelScope.launch(Dispatchers.IO + errorHandler) {
//        val data = getCoinsListingsUseCase.execute().map { it.toPresentation() }
//        coins.addAll(data)
//    }

    fun getNews() = viewModelScope.launch {
        val shouldLoadData =
            startIndex == 1 ||
                    (startIndex != 1 && canPaginate) && listState == ListState.IDLE

        if (shouldLoadData) {
            listState = if (startIndex == 1) ListState.LOADING else ListState.PAGINATING

            getCoinsListingsUseCase.executePaging(startIndex, 20)
                .catch { listState = ListState.ERROR }
                .collect { result ->
                    canPaginate = startIndex < 201
                    val list = result.map { it.toPresentation() }

                    if (startIndex == 1) {
                        coins.clear()
                        coins.addAll(list)
                    } else {
                        coins.addAll(list)
                    }

                    listState = ListState.IDLE

                    if (canPaginate) {
                        startIndex += 20
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

    /**
     *  // UPDATE
     */

    init {
        getNews()
        //getNoPaging()
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