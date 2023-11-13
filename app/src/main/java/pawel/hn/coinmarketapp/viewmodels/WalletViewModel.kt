package pawel.hn.coinmarketapp.viewmodels


import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.database.CoinEntity
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.showLogN
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(private val coinRepository: CoinRepository) :
    ViewModel() {



    private val _eventProgressBar = MutableLiveData(false)
    val eventProgressBar: LiveData<Boolean>
        get() = _eventProgressBar

    private val _eventErrorResponse = MutableLiveData<Boolean>()
    val eventErrorResponse: LiveData<Boolean>
        get() = _eventErrorResponse

    fun calculateTotalBalance(list: List<Wallet>): Double = list.sumOf { it.total }

    fun totalWallet(list: List<Wallet>): List<Wallet> {

        val totalList = mutableListOf<Wallet>()
        val listOfCoinIds = mutableListOf<Int>()

        for (coinLoop in list) {
            if (listOfCoinIds.contains(coinLoop.coinId)) {
                continue
            } else {
                listOfCoinIds.add(coinLoop.coinId)
                val tempList = list.filter { it.coinId == coinLoop.coinId }
                val newVolume = tempList.sumOf { it.volume }
                val newTotal = tempList.sumOf { it.total }
                totalList.add(
                    Wallet(
                        coinId = coinLoop.coinId,
                        name = coinLoop.name,
                        symbol = coinLoop.symbol,
                        volume = newVolume,
                        price = coinLoop.price,
                        total = newTotal,
                        walletNo = 3
                    )
                )
            }
        }

        return totalList.sortedByDescending { it.total }
    }

    fun onTaskSwiped(coin: Wallet) = viewModelScope.launch {
        //   repository.wallet.deleteFromWallet(coin)
    }

    fun refreshData(ccy: String) = viewModelScope.launch {
        _eventProgressBar.value = true
        //   repository.getCoinsData(ccy)
        _eventProgressBar.value = false
        //   _eventErrorResponse.value = repository.responseError
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


    fun setChart(list: List<Wallet>, pieChart: PieChart, context: Context) {
        val entries = ArrayList<PieEntry>()
        if (!list.isNullOrEmpty()) {
            list.forEach {
                entries.add(
                    PieEntry(it.total.toFloat(), it.symbol)
                )
            }
        }

        val setData = PieDataSet(entries, "Wallet")
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
        val colorList = colorIds.map {
            ContextCompat.getColor(context, it)
        }

        setData.apply {
            colors = colorList
            sliceSpace = 2f
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            valueTextColor = R.color.coinsListHeader
            valueTextSize = 12f
            valueFormatter = PercentFormatter(pieChart)
            valueLinePart1OffsetPercentage = 80f
            valueLinePart1Length = 0.3f
            valueLinePart2Length = 0.4f
        }

        val dataPie = PieData(setData)
        dataPie.setDrawValues(true)
        pieChart.apply {
            data = dataPie
            setUsePercentValues(true)
            description.isEnabled = false
            legend.isEnabled = false
            setEntryLabelTextSize(14f)
            setEntryLabelTypeface(Typeface.MONOSPACE)
            isDrawHoleEnabled = true
            setEntryLabelColor(R.color.coinsListHeader)
            setNoDataText("Empty wallet")
            invalidate()
        }
    }
}