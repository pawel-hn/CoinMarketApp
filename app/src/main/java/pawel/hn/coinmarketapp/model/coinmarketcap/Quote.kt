package pawel.hn.coinmarketapp.model.coinmarketcap

import com.google.gson.annotations.SerializedName


data class Quote(
    @SerializedName("USD")
    val USD: USD,
    @SerializedName("EUR")
    val EUR: EUR,
    @SerializedName("PLN")
    val PLN: PLN
)