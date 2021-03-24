package pawel.hn.coinmarketapp.model

data class Data(
    val id: Int,
    val name: String,
    val quote: Quote,
    val symbol: String,
)