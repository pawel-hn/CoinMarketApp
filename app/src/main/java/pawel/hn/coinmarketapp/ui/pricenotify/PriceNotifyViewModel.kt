package pawel.hn.coinmarketapp.ui.pricenotify

import android.content.Context
import androidx.lifecycle.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Notifications
import pawel.hn.coinmarketapp.notification.NotifyWorker
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PriceNotifyViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext context: Context
) : ViewModel() {

    private val _latestPrice = MutableLiveData<Double>()
    val latestPrice: LiveData<Double>
        get() = _latestPrice

    private val _notificationOnOff = MutableLiveData<Boolean>()
    val notificationOnOff: LiveData<Boolean>
        get() = _notificationOnOff

    val notifications = repository.coins.notifications

    private var priceAlertData: Int = 0
    private lateinit var workManager: WorkManager
    private lateinit var workRequest: PeriodicWorkRequest


    init {
        getLatestPrice()
        setUpWorkManager(context)
    }

    fun notifyWorker(notificationOnOff: Boolean) {
        if (notificationOnOff) {
            workManager.enqueueUniquePeriodicWork(PRICE_ALERT,
                ExistingPeriodicWorkPolicy.REPLACE, workRequest)
            val notification = Notifications(workRequest.id.toString(), false)
            viewModelScope.launch {
                repository.coins.insertNotifications(notification)
            }
        } else {
            workManager.cancelUniqueWork(PRICE_ALERT)
        }
    }


    private fun setUpWorkManager(context: Context) {
        showLog("viewModel setUpWorkManager")
        workManager = WorkManager.getInstance(context)
    }


    fun setPriceAlert(priceAlert: Int) {
        showLog("viewModel setPriceAlert")
        priceAlertData = priceAlert
        if (notificationOnOff.value == true) {
            notifyWorker(true)
        }
        setCurrentPriceAlert(priceAlertData)
    }

    fun setCurrentPriceAlert(alertPrice: Int) {
        showLog("viewModel setCurrentPriceAlert")
        priceAlertData = alertPrice
        workRequest = PeriodicWorkRequestBuilder<NotifyWorker>(10, TimeUnit.MINUTES)
            .setInitialDelay(15, TimeUnit.SECONDS)
            .setInputData(setDataForWorker(priceAlertData))
            .build()

    }

    private fun getLatestPrice() {
        viewModelScope.launch {
            val price = repository.getLatestBitcoinPrice()
            price?.let {
                _latestPrice.postValue(it)
            }
        }
    }

    fun setNotificationOn() {
        _notificationOnOff.postValue(true)
    }

    fun setNotificationOff() {
        _notificationOnOff.postValue(false)
    }

    private fun setDataForWorker(msg: Int): Data {
        return Data.Builder().putInt(PRICE_ALERT_INPUT, msg).build()
    }
}