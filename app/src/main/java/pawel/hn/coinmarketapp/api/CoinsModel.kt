package pawel.hn.coinmarketapp.api


data class CoinsModel(
        val data: List<Data>,
        val status: Status
) {
    data class Status(
            val error_code: Int,
            val error_message: String,
            val credit_count: Int
    )

    data class Data(
            val id: Int,
            val name: String,
            val symbol: String,
            val quote: Quotes
          ) {

        data class Quotes(
                val USD: Usd
        ) {
            data class Usd(
                    val price: Double,
                    val percent_change_24h: Double
            )
        }
    }
}



