package pawel.hn.coinmarketapp.model


import com.google.gson.annotations.SerializedName

data class USD(
    @SerializedName("percent_change_24h")
    val percentChange24h: Double,
    val price: Double,
)