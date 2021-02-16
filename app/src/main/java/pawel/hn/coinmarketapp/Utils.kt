package pawel.hn.coinmarketapp

import androidx.appcompat.widget.SearchView
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.database.Coin

fun CoinApi.Data.toCoinsWithCheckBox() = Coin(
    coinId = this.id,
    name = this.name,
    symbol =this.symbol,
    favourite = false,
    price =  this.quote.USD.price,
    change24h =  this.quote.USD.percent_change_24h
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