package pawel.hn.coinmarketapp.model.coinmarketcap


/**
 * ApiResponse used for getting single coin data from CoinMarketCap and
 * modeling Json data through Gson converter in Retrofit
 */
data class ApiResponse(
    val `data`: Map<Int, Data>,
    val status: Status
)


/**
 * ApiResponse used for getting list of coins from CoinMarketCap and
 * modeling Json data through Gson converter in Retrofit
 */
data class ApiResponseArray(
    val `data`: List<Data>,
    val status: Status
)