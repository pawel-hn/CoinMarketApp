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
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.util.CHANNEL_ID
import pawel.hn.coinmarketapp.util.formatter
import pawel.hn.coinmarketapp.util.showLog


@HiltWorker
class NotifyWorker @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workParams: WorkerParameters,
    private val coinApi: CoinApi
) : CoroutineWorker(context, workParams) {



    private val priceToCompare = 50000
    private val id = "1"
    private var newPrice: Double? = null
    private var notificationID = 2


    override suspend fun doWork(): Result {
        showLog("doWork called")
        var notificationSend = false
        try {
            withContext(Dispatchers.IO) {
                val response = coinApi.getLatestSingleQuote(id)
                if (response.isSuccessful) {
                    showLog("doWork: response success")
                    response.body()?.let { ApiResponse ->
                        newPrice = ApiResponse.data[id.toInt()]?.quote?.USD?.price
                        newPrice?.let {
                            notificationSend = if (newPrice!! > priceToCompare) {
                                sendNotification("price above")
                                true
                            } else {
                                sendNotification("price below")
                                true
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            showLog("Exception: ${e.message}")
            return Result.retry()
        }
        return if (notificationSend) {
            showLog("result success")
            Result.success()
        } else {
            showLog("result retry")
            Result.retry()
        }

    }

    private fun sendNotification(msg: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Bitcoin price alert")
            .setContentText("$msg $priceToCompare, its ${formatter.format(newPrice)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_bitcoin)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(notificationID, notification)
        notificationID++
    }


}