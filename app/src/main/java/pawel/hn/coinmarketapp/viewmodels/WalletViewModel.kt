package pawel.hn.coinmarketapp.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.database.CoinEntity
import pawel.hn.coinmarketapp.database.WalletEntity
import pawel.hn.coinmarketapp.domain.WalletCoin
import pawel.hn.coinmarketapp.usecase.ObserveWalletUseCase
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.errorHandler
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(private val observeWalletUseCase: ObserveWalletUseCase) :
    ViewModel() {

    private val _walletCoins = MutableStateFlow<List<WalletCoin>>(emptyList())
    val walletCoins = _walletCoins
        .map { Resource.Success(it) }
        .catch { Resource.Error<List<WalletCoin>>("wallet error") }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(2000), Resource.Loading()
        )


    init {
        observeWallet()
    }


    private val _eventProgressBar = MutableLiveData(false)
    val eventProgressBar: LiveData<Boolean>
        get() = _eventProgressBar

    private val _eventErrorResponse = MutableLiveData<Boolean>()
    val eventErrorResponse: LiveData<Boolean>
        get() = _eventErrorResponse

    fun calculateTotalBalance(list: List<WalletEntity>): Double = 100.0


    fun totalWallet(list: List<WalletEntity>): List<WalletEntity> {

        return emptyList()
    }

    fun observeWallet() = viewModelScope.launch(Dispatchers.IO + errorHandler) {
        observeWalletUseCase.build().collectLatest {
            _walletCoins.value = it
        }
    }

    fun onTaskSwiped(coin: WalletEntity) = viewModelScope.launch {
        //   repository.wallet.deleteFromWallet(coin)
    }


    fun walletRefresh(list: List<CoinEntity>) = viewModelScope.launch {

        val listTemp = list.filter { coin ->
            //coin.name == walletLiveData.value?.find { it.name == coin.name }?.name
            true
        }
        if (listTemp.isNullOrEmpty()) {
            _eventProgressBar.value = false
            return@launch
        }

    }

    fun deleteAll() {
        viewModelScope.launch {
            //    repository.wallet.deleteAllFromWallets()
        }
    }


    fun setChart() {

        val colorIds = listOf(
            R.color.chartColor1,
            R.color.chartColor2,
            R.color.chartColor3,
            R.color.chartColor4,
            R.color.chartColor5,
            R.color.chartColor6,
            R.color.chartColor7,
            R.color.chartColor8,
            R.color.chartColor9
        )
    }
}
