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
import pawel.hn.coinmarketapp.util.PRICE_ALERT
import pawel.hn.coinmarketapp.util.PRICE_ALERT_INPUT
import pawel.hn.coinmarketapp.util.showLog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PriceNotifyViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext context: Context
) : ViewModel() {

    private val _latestPrice = MutableLiveData<String>()
    val latestPrice: LiveData<String>
        get() = _latestPrice

    private val _notificationOnOff = MutableLiveData<Boolean>()
    val notificationOnOff: LiveData<Boolean>
        get() = _notificationOnOff

    val notifications = repository.notifications

    private var priceAlertData: Double = 2.0
    lateinit var workManager: WorkManager
    lateinit var workRequest: PeriodicWorkRequest


    init {
        getLatestPrice()
        setUpWorkManager(context)
    }

    fun notifyWorker(notificationOnOff: Boolean) {
        showLog("notifyWorker $notificationOnOff")
        if (notificationOnOff) {
            workManager.enqueueUniquePeriodicWork(PRICE_ALERT,
                ExistingPeriodicWorkPolicy.REPLACE, workRequest)
            val notification = Notifications(workRequest.id.toString(), false)
            viewModelScope.launch {
                repository.insertNotifications(notification)
                showLog("noti: ${notifications.value?.size}")
            }
        } else {
            workManager.cancelUniqueWork(PRICE_ALERT)
        }
    }


    private fun setUpWorkManager(context: Context) {
        showLog("viewModel setUpWorkManager")
        workManager = WorkManager.getInstance(context)
    }


    fun setPriceAlert(priceAlert: Double) {
        showLog("viewModel setPriceAlert")
        priceAlertData = priceAlert
        if (notificationOnOff.value == true) {
            notifyWorker(true)
        }
        setCurrentPriceAlert(priceAlertData.toString())
    }

    fun setCurrentPriceAlert(alertPrice: String) {
        showLog("viewModel setCurrentPriceAlert")
        priceAlertData = alertPrice.toDouble()
        workRequest = PeriodicWorkRequestBuilder<NotifyWorker>(10, TimeUnit.MINUTES)
            .setInitialDelay(15, TimeUnit.SECONDS)
            .setInputData(setDataForWorker(priceAlertData))
            .build()

    }

    private fun getLatestPrice() {
        viewModelScope.launch {
            val price = repository.getLatestBitcoinPrice()
            price?.let {
                _latestPrice.postValue(it.toString())
            }
        }
    }

    fun setNotificationOn() {
        _notificationOnOff.postValue(true)
    }

    fun setNotificationOff() {
        _notificationOnOff.postValue(false)
    }

    private fun setDataForWorker(msg: Double): Data {
        return Data.Builder().putDouble(PRICE_ALERT_INPUT, msg).build()
    }
}