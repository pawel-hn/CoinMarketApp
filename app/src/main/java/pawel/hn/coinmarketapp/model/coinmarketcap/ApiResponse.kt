package pawel.hn.coinmarketapp.model.coinmarketcap

import com.google.gson.annotations.SerializedName


data class ApiResponse(

    @SerializedName("data")
    val coinData: Map<Int, CoinData>,

    @SerializedName("status")
    val status: Status
)

data class ApiResponseArray(

    @SerializedName("data")
    val coinData: List<CoinData>,

    @SerializedName("status")
    val status: Status
)