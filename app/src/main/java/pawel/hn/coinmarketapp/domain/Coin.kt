package pawel.hn.coinmarketapp.domain

import android.net.Uri
import pawel.hn.coinmarketapp.compose.WalletMain
import pawel.hn.coinmarketapp.database.Wallet

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


fun Coin.toWalletCoin(volume: Double) = Wallet(
    id = coinId,
    coinId = coinId,
    name = name,
    symbol = symbol,
    volume = volume,
    price = 1000.0,
    total = 500.0,
    walletNo = 0,
)