package pawel.hn.coinmarketapp.domain

import android.net.Uri
import pawel.hn.coinmarketapp.database.WalletEntity

data class Coin(
    val coinId: Int,
    val name: String,
    val symbol: String,
    val favourite: Boolean,
    val price: Double,
    val change24h: String,
    val isChange24hUp: Boolean,
    val change7d: String,
    val isChange7dUp: Boolean,
    val cmcRank: Int,
    val imageUri: Uri
)


fun Coin.toWalletDatabase(volume: Double) = WalletEntity(
    coinId = coinId,
    volume = volume,
    walletNo = 0,
)