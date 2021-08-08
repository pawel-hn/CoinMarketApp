package pawel.hn.coinmarketapp.model.coinmarketcap

import com.google.gson.annotations.SerializedName
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.util.CURRENCY_PLN
import pawel.hn.coinmarketapp.util.CURRENCY_USD

data class CoinData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("quote")
    val quote: Quote,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("cmc_rank")
    val cmcRank: Int
) {

  fun apiResponseConvertToCoin(ccy: String): Coin {
        val price = when(ccy) {
            CURRENCY_USD -> this.quote.USD.price
            CURRENCY_PLN -> this.quote.PLN.price
            else -> this.quote.EUR.price
        }

        val change24h = when(ccy) {
            CURRENCY_USD -> this.quote.USD.percentChange24h
            CURRENCY_PLN -> this.quote.PLN.percentChange24h
            else -> this.quote.EUR.percentChange24h
        }

        val change7d = when(ccy) {
            CURRENCY_USD -> this.quote.USD.percentChange7d
            CURRENCY_PLN -> this.quote.PLN.percentChange7d
            else -> this.quote.EUR.percentChange7d
        }

        return Coin(
            coinId = this.id,
            name = this.name,
            symbol = this.symbol,
            favourite = false,
            price = price,
            change24h = change24h,
            change7d = change7d,
            cmcRank = this.cmcRank
        )
    }
}