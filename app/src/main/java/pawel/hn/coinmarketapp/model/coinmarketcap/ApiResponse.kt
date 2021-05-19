package pawel.hn.coinmarketapp.model.coinmarketcap

import pawel.hn.coinmarketapp.model.Data
import pawel.hn.coinmarketapp.model.Status


data class ApiResponse(
    val `data`: Map<Int, Data>,
    val status: Status
)

data class ApiResponseArray(
    val `data`: List<Data>,
    val status: Status
)