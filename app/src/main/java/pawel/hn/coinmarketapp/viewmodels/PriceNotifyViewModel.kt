package pawel.hn.coinmarketapp.viewmodels

import android.content.Context
import androidx.lifecycle.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.database.Notifications
import pawel.hn.coinmarketapp.notification.NotifyWorker
import pawel.hn.coinmarketapp.repository.CoinsRepository
import pawel.hn.coinmarketapp.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PriceNotifyViewModel @Inject constructor(
    private val coinsRepository: CoinsRepository,
    @ApplicationContext context: Context
) : ViewModel() {


     val latestPrice: LiveData<Double> = MutableLiveData()
     val notificationOnOff: LiveData<Boolean> = MutableLiveData()

    val notifications = coinsRepository.coins.notifications
    private var priceAlertData: Double = 0.0
    private lateinit var workManager: WorkManager
    private lateinit var workRequest: PeriodicWorkRequest


    init {
        getLatestPrice()
        setUpWorkManager(context)
    }

    fun notifyWorker(notificationOnOff: Boolean) {
        if (notificationOnOff) {
            workManager.enqueueUniquePeriodicWork(
                PRICE_ALERT,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
            val notification = Notifications(workRequest.id.toString(), false)
            viewModelScope.launch {
                coinsRepository.coins.insertNotifications(notification)
            }
        } else {
            workManager.cancelUniqueWork(PRICE_ALERT)
        }
    }

    private fun setUpWorkManager(context: Context) {
        workManager = WorkManager.getInstance(context)
    }

    fun setPriceAlert(priceAlert: Int) {
        priceAlertData = priceAlert.toDouble()
        setCurrentPriceAlert(priceAlertData)
        if (notificationOnOff.value == true) {
            notifyWorker(true)
        }
    }

    private fun setCurrentPriceAlert(alertPrice: Double) {
        priceAlertData = alertPrice
        workRequest = PeriodicWorkRequestBuilder<NotifyWorker>(10, TimeUnit.MINUTES)
            .setInitialDelay(15, TimeUnit.SECONDS)
            .setInputData(setDataForWorker(priceAlertData))
            .build()
    }

    private fun getLatestPrice() {
        viewModelScope.launch {
            val price = coinsRepository.getLatestBitcoinPrice()
            price?.let {
                latestPrice.toMutableLiveData().postValue(it)
            }
        }
    }


    fun setNotificationOn() {
        notificationOnOff.toMutableLiveData().postValue(true)
    }

    fun setNotificationOff() {
        notificationOnOff.toMutableLiveData().postValue(false)
    }

    private fun setDataForWorker(priceAlert: Double): Data {
        return Data.Builder().putDouble(PRICE_ALERT_INPUT, priceAlert).build()
    }
}