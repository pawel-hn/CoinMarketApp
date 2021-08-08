package pawel.hn.coinmarketapp.model.coinmarketcap


import com.google.gson.annotations.SerializedName

data class USD(
    @SerializedName("percent_change_24h")
    val percentChange24h: Double,
    @SerializedName("percent_change_7d")
    val percentChange7d: Double,
    @SerializedName("price")
    val price: Double
)