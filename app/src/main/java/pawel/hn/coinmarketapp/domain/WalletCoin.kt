package pawel.hn.coinmarketapp.domain

data class WalletCoin(
    val coin: Coin,
    val volume: Double,
    val walletNo: Int
)
