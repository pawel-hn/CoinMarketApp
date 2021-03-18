package pawel.hn.coinmarketapp.util

import android.util.Log
import androidx.appcompat.widget.SearchView
import pawel.hn.coinmarketapp.api.CoinResponse
import pawel.hn.coinmarketapp.database.Coin
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


const val TAG = "PHN"


var locale: Locale = Locale.getDefault()
val numberUtil: NumberFormat = NumberFormat.getInstance(locale)
val formatter = DecimalFormat("#,##0.0#")
val formatterTotal = DecimalFormat("##,###")

fun CoinResponse.Data.apiResponseConvertToCoin() = Coin(
    coinId = this.id,
    name = this.name,
    symbol = this.symbol,
    favourite = false,
    price = this.quote.USD.price,
    change24h = this.quote.USD.percent_change_24h
)

inline fun SearchView.onQueryTextChanged(crossinline listener:(String) -> Unit) {
    this.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean = true

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })

}

fun showLog(string: String) = Log.d(TAG, string)