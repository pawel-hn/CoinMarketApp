package pawel.hn.coinmarketapp.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.activity.MainActivity
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.util.*

/**
 * WorManager, if price alert is on, this class is responsible for background connection with CoinMarketCap,
 * checking if price alert criteria is met, if yes, worker sends notification and clear notification database,
 * which turns switch OFF.
 */
@HiltWorker
class NotifyWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workParams: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(context, workParams) {

    private var notificationID = 2
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val currency: String = sharedPreferences.getString(
        context.getString(R.string.settings_currency_key),
        CURRENCY_USD
    )!!

    override suspend fun doWork(): Result {
        val currentPriceAlert = workParams.inputData
            .getDouble(PRICE_ALERT_INPUT, 10000.0)

        showLog("doWork: $currentPriceAlert")

        withContext(Dispatchers.IO) {
            val newPrice = repository.getLatestBitcoinPrice()

            if (newPrice != null && newPrice > currentPriceAlert) {
                sendNotification(
                    "price above: " +
                            "${formatPriceAndVolForView(currentPriceAlert,
                                ValueType.Fiat,
                            currency)}," +
                            " it's ${formatPriceAndVolForView(newPrice, ValueType.Fiat, currency)}"
                )
                repository.coins.deleteNotification()
            }
        }
        return Result.success()
    }

    private fun sendNotification(msg: String) {

        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent
            .getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Bitcoin price alert:")
            .setContentText(msg)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_bitcoin_white)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.bitcoin_logo))
            .build()

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(notificationID, notification)
        notificationID++
    }


}