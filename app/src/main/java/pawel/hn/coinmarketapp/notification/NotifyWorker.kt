package pawel.hn.coinmarketapp.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.util.CHANNEL_ID
import pawel.hn.coinmarketapp.util.PRICE_ALERT_INPUT
import pawel.hn.coinmarketapp.util.showLog


@HiltWorker
class NotifyWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workParams: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(context, workParams) {

    private var notificationID = 2

    override suspend fun doWork(): Result {
        showLog("doWork called")

        val currentPriceAlert = workParams.inputData
            .getDouble(PRICE_ALERT_INPUT, 10000.0)

        showLog("worker input: $currentPriceAlert")

        withContext(Dispatchers.IO) {
            val newPrice = repository.getLatestBitcoinPrice()
            showLog("worker newprice: $newPrice")
            if (newPrice != null && newPrice > currentPriceAlert) {
                sendNotification("price above $currentPriceAlert, it's $newPrice")
                repository.deleteNotification(id.toString())
            }
        }

        return Result.success()
    }

    private fun sendNotification(msg: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Bitcoin price alert")
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_bitcoin)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(notificationID, notification)
        notificationID++
    }


}