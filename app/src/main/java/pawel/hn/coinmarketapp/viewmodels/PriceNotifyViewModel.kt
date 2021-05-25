package pawel.hn.coinmarketapp.viewmodels

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

    /**
     * Livedata with latest price, presented to the user.
     */
    private val _latestPrice = MutableLiveData<Double>()
    val latestPrice: LiveData<Double>
        get() = _latestPrice

    /**
     * Livedata which reflects state of switch in the NotifyFragment.
     */
    private val _notificationOnOff = MutableLiveData<Boolean>()
    val notificationOnOff: LiveData<Boolean>
        get() = _notificationOnOff

    val notifications = repository.coins.notifications
    private var priceAlertData: Double = 0.0
    private lateinit var workManager: WorkManager
    private lateinit var workRequest: PeriodicWorkRequest


    init {
        getLatestPrice()
        setUpWorkManager(context)
    }

    /**
     * Based on state of switch this functions calls worker to check if price alert criteria is met or
     * to cancel worker.
     */
    fun notifyWorker(notificationOnOff: Boolean) {
        if (notificationOnOff) {
            workManager.enqueueUniquePeriodicWork(
                PRICE_ALERT,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
            val notification = Notifications(workRequest.id.toString(), false)
            viewModelScope.launch {
                repository.coins.insertNotifications(notification)
            }
        } else {
            workManager.cancelUniqueWork(PRICE_ALERT)
        }
    }

    private fun setUpWorkManager(context: Context) {
        workManager = WorkManager.getInstance(context)
    }


    /**
     * Called from fragment in onCreateView to get previously set alarm and called
     * when user sets new price alert. If notification switch is ON, Worker is updated.
     */
    fun setPriceAlert(priceAlert: Int) {
        priceAlertData = priceAlert.toDouble()
        setCurrentPriceAlert(priceAlertData)
        if (notificationOnOff.value == true) {
            notifyWorker(true)
        }
    }

    /**
     * Called in setPriceAlert() in order to prepare work request for work manager.
     */
    private fun setCurrentPriceAlert(alertPrice: Double) {
        priceAlertData = alertPrice
        workRequest = PeriodicWorkRequestBuilder<NotifyWorker>(10, TimeUnit.MINUTES)
            .setInitialDelay(15, TimeUnit.SECONDS)
            .setInputData(setDataForWorker(priceAlertData))
            .build()
    }

    /**
     * Called to get latest btc price and display it in the fragment
     */
    private fun getLatestPrice() {
        viewModelScope.launch {
            val price = repository.getLatestBitcoinPrice()
            price?.let {
                _latestPrice.postValue(it)
            }
        }
    }

    /**
     * Called when switch is set to ON (true)
     */
    fun setNotificationOn() {
        _notificationOnOff.postValue(true)
    }

    /**
     * Called when switch is set to OFF (false)
     * and when table with notifications is empty
     */
    fun setNotificationOff() {
        _notificationOnOff.postValue(false)
    }

    private fun setDataForWorker(priceAlert: Double): Data {
        return Data.Builder().putDouble(PRICE_ALERT_INPUT, priceAlert).build()
    }
}