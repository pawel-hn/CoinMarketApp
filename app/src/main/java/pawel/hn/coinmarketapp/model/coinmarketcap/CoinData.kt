package pawel.hn.coinmarketapp.model.coinmarketcap

import com.google.gson.annotations.SerializedName

data class CoinData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("quote")
    val quote: Quote,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("cmc_rank")
    val cmcRank: Int
)