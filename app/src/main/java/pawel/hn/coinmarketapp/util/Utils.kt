package pawel.hn.coinmarketapp.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
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
import java.math.RoundingMode
import java.text.DecimalFormat

enum class ValueType(val pattern: String) {
    Crypto(COIN_PATTERN),
    Fiat(FIAT_PATTERN),
}

fun formatPriceAndVolForView(volume: Double, type: ValueType): SpannableString {
    val df = DecimalFormat(type.pattern)
    df.roundingMode = RoundingMode.DOWN

    val spannablePrice = SpannableString("$${df.format(volume)}")
    val spannableVol = SpannableString(df.format(volume))
    val dollarColor = ForegroundColorSpan(
        Color.GRAY
    )
    spannablePrice.setSpan(dollarColor, 0,1, Spanned.SPAN_COMPOSING)


    return if (type == ValueType.Fiat) {
        spannablePrice
    } else {
       spannableVol
    }
}


fun Data.apiResponseConvertToCoin() = Coin(
    coinId = this.id,
    name = this.name,
    symbol = this.symbol,
    favourite = false,
    price = this.quote.USD.price,
    change24h = this.quote.USD.percentChange24h,
    change7d = this.quote.USD.percentChange7d,
    cmcRank = this.cmcRank
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
    Snackbar.make(view, text, Snackbar.LENGTH_SHORT).apply {
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
    val priceString = price.toString()

    return if (priceString.substring(priceString.indexOf('.')).length > 2) {
        priceString.substring(0, priceString.indexOf('.') + 3) + " %"
    } else {
        "$priceString %"
    }
}

fun setPriceChangeColor(view: TextView, change: Double) {
    if (change < 0.00) {
        view.setTextColor(ContextCompat.getColor(view.context, R.color.priceDrop))
    } else {
        view
            .setTextColor(
                ContextCompat.getColor(
                    view.context, R.color.priceUp
                )
            )
    }
}


