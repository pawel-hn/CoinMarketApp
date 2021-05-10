package pawel.hn.coinmarketapp.model

import com.google.gson.annotations.SerializedName

data class Data(
    val id: Int,
    val name: String,
    val quote: Quote,
    val symbol: String,
    @SerializedName("cmc_rank")
    val cmcRank: Int
)