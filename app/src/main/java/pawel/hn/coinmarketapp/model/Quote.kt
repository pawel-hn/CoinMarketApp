package pawel.hn.coinmarketapp.model


import com.google.gson.annotations.SerializedName

data class Quote(
    val USD: USD,
    val EUR: EUR,
    val PLN: PLN
)