package pawel.hn.coinmarketapp.domain

import android.net.Uri

data class Coin(
    val coinId: Int,
    val name: String,
    val symbol: String,
    val favourite: Boolean,
    val price: String,
    val change24h: String,
    val isChange24hUp: Boolean,
    val change7d: String,
    val isChange7dUp: Boolean,
    val cmcRank: Int,
    val imageUri: Uri
)
