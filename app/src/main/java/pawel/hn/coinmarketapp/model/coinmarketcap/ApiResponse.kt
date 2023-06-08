package pawel.hn.coinmarketapp.model.coinmarketcap

import com.google.gson.annotations.SerializedName
import pawel.hn.coinmarketapp.database.Coin
import kotlin.random.Random

data class ApiResponse(
    val coinResponse: Map<Int, CoinResponse>,
    val status: Status
)

data class ApiResponseCoins(
    @SerializedName("data") val coins: List<CoinResponse>,
    @SerializedName("status") val status: Status
)

data class CoinResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("quote") val quote: Quote,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("cmc_rank") val cmcRank: Int
)

data class Quote(
    @SerializedName("USD") val USD: USD,
    @SerializedName("EUR") val EUR: EUR,
    @SerializedName("PLN") val PLN: PLN
)

data class USD(
    @SerializedName("percent_change_24h") val percentChange24h: Double,
    @SerializedName("percent_change_7d") val percentChange7d: Double,
    @SerializedName("price") val price: Double
)

data class EUR(
    @SerializedName("percent_change_24h") val percentChange24h: Double,
    @SerializedName("percent_change_7d") val percentChange7d: Double,
    @SerializedName("price") val price: Double
)

data class PLN(
    @SerializedName("percent_change_24h") val percentChange24h: Double,
    @SerializedName("percent_change_7d") val percentChange7d: Double,
    @SerializedName("price") val price: Double
)

data class Status(
    @SerializedName("credit_count") val creditCount: Int,
    @SerializedName("elapsed") val elapsed: Int,
    @SerializedName("error_code") val errorCode: Int,
    @SerializedName("error_message") val errorMessage: String,
    @SerializedName("timestamp") val timestamp: String
)

fun ApiResponseCoins.toDomain(): List<Coin> = coins.toDomain()

fun List<CoinResponse>.toDomain(): List<Coin> = map { it.toDomain() }

fun CoinResponse.toDomain(): Coin = Coin(
    name = this.name,
    symbol = this.symbol,
    price = this.quote.USD.price,
    change24h = this.quote.USD.percentChange24h,
    change7d = this.quote.USD.percentChange7d,
    cmcRank = this.cmcRank,
    favourite = false,
    coinId = this.id
)

