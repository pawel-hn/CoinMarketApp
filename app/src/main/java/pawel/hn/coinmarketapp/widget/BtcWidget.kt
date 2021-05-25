package pawel.hn.coinmarketapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.activity.MainActivity
import pawel.hn.coinmarketapp.model.coinmarketcap.Data
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BtcWidget : AppWidgetProvider() {

    @Inject
    lateinit var repository: Repository

    var btc: Data? = null

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        showLog("widget onReceive called")

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context!!, BtcWidget::class.java))
        coroutineScope.launch {
            btc = repository.getBitcoinData()!!
            ids.forEach { id ->
                updateAppWidget(
                    context,
                    appWidgetManager,
                    id
                )
            }
        }
    }


    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        showLog("widget onUpdate called")

        appWidgetIds?.forEach { appWidgetId ->
            val views = RemoteViews(context?.packageName, R.layout.widget)
            val refresh = Intent(context, BtcWidget::class.java)
            val refreshIntent = PendingIntent
                .getBroadcast(context, 0, refresh, PendingIntent.FLAG_UPDATE_CURRENT)

            views.setOnClickPendingIntent(R.id.widget_refresh, refreshIntent)
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }

    }


    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.widget)

        btc?.let { it ->
            val price = it.quote.USD.price
            val change24h = it.quote.USD.percentChange24h
            val change7d = it.quote.USD.percentChange7d
            val nameId = R.id.text_view_name_widget
            val symbolId = R.id.text_view_symbol_widget
            val priceId = R.id.text_view_price_coin_widget
            val change24hId = R.id.text_view_24h_change_widget
            val change7dId = R.id.text_view_7d_change_widget


            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }

            views.apply {
                setTextViewText(priceId, formatPriceAndVolForView(
                    price,
                    ValueType.Fiat,
                    CURRENCY_USD
                ))

                setTextViewText(change24hId,"24h ${formatPriceChange(change24h)}")
                setTextViewText(change7dId,"7d ${formatPriceChange(change7d)}")
                setTextColor(priceId, Color.BLACK)
                setTextViewTextSize(priceId, COMPLEX_UNIT_SP, 20f)
                setTextViewTextSize(nameId, COMPLEX_UNIT_SP, 20f)
                setTextViewTextSize(change24hId, COMPLEX_UNIT_SP, 12f)
                setTextViewTextSize(change7dId, COMPLEX_UNIT_SP, 12f)
                setTextViewText(nameId, "Bitcoin")
                setTextViewText(symbolId, "BTC")
                setTextColor(priceId, Color.BLACK)

                setTextColor(change24hId, if (change24h < 0.0) Color.RED else Color.GREEN)
                setTextColor(change7dId, if (change7d < 0.0) Color.RED else Color.GREEN)

                setOnClickPendingIntent(R.id.layout_widget, pendingIntent)
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }


    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        job.cancel()
    }


}