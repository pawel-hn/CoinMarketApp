package pawel.hn.coinmarketapp.model.coinmarketcap


data class ApiResponse(
    val `data`: Map<Int, Data>,
    val status: Status
)

data class ApiResponseArray(
    val `data`: List<Data>,
    val status: Status
)