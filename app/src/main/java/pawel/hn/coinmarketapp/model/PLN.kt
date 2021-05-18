package pawel.hn.coinmarketapp.model


import com.google.gson.annotations.SerializedName

data class PLN(
    @SerializedName("percent_change_24h")
    val percentChange24h: Double,
    @SerializedName("percent_change_7d")
    val percentChange7d: Double,
    val price: Double
)