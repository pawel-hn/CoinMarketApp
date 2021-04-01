package pawel.hn.coinmarketapp.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.model.Data
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


var locale: Locale = Locale.getDefault()
val numberUtil: NumberFormat = NumberFormat.getInstance(locale)
val formatter = DecimalFormat("#,##0.0#")
val formatterTotal = DecimalFormat("##,###")

fun Data.apiResponseConvertToCoin() = Coin(
    coinId = this.id,
    name = this.name,
    symbol = this.symbol,
    favourite = false,
    price = this.quote.USD.price,
    change24h = this.quote.USD.percentChange24h,
    change7d = this.quote.USD.percentChange7d
)

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean = true

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}

fun showSnack(view: View, text: String) {
    Snackbar.make(view, text, Snackbar.LENGTH_SHORT).apply{
        this.view.apply {
            setBackgroundColor(ContextCompat.getColor(view.context, R.color.snackBarBackground))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        show()
    }
}

fun showLog(string: String) = Log.d(TAG, string)

fun SharedPreferences.put(action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    action(editor)
    editor.apply()
}

fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun formatPriceChange(price: Double): String {
    val priceString  = price.toString()

    return if (priceString.substring(priceString.indexOf('.')).length > 2) {
        priceString.substring(0, priceString.indexOf('.') + 3) + " %"
    } else {
        "$priceString %"
    }
}

fun setPriceChangeColor(view: TextView, change: Double) {
    if (change < 0.00) {
        view.setTextColor(Color.RED)
    } else {
        view
            .setTextColor(ContextCompat.getColor(
                view.context, R.color.design_default_color_primary_variant
            ))}
}


