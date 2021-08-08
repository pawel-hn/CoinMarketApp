package pawel.hn.coinmarketapp.data

import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.database.WalletDao
import javax.inject.Inject


class WalletData @Inject constructor(private val walletDao: WalletDao) {

    val wallet = walletDao.getWallet()

    suspend fun addToWallet(newCoin: Wallet) {
        val oldCoin =
            wallet.value?.find { it.coinId == newCoin.coinId && it.walletNo == newCoin.walletNo }

        if (oldCoin != null) {
            val newVolume = newCoin.volume + oldCoin.volume
            updateWallet(
                oldCoin.copy(volume = newVolume),
                oldCoin.price
            )
        } else {
            walletDao.insertIntoWallet(newCoin)
        }
    }

    suspend fun deleteFromWallet(coin: Wallet) {
        walletDao.deleteFromWallet(coin)

    }
    suspend fun deleteAllFromWallets() {
        walletDao.deleteAllFromWallets()
    }

    suspend fun updateWallet(coin: Wallet, newPrice: Double) {
        val newTotal = coin.volume * newPrice

        walletDao.updateWallet(
            coin.copy(
                price = newPrice, total = newTotal
            )
        )
    }


    fun createWalletCoin(
        coinName: String,
        coinVolume: Double,
        walletNo: Int,
        coins: List<Coin>
    ): Wallet {
        val price = coins.find { it.name == coinName }?.price ?: 0.0
        val coinId = coins.find { it.name == coinName }?.coinId ?: 1
        val symbol = coins.find { it.name == coinName }?.symbol ?: ""
        val total = price * coinVolume

        return Wallet(
            coinId = coinId,
            name = coinName,
            symbol = symbol,
            volume = coinVolume,
            price = price,
            total = total,
            walletNo = walletNo
        )
    }


}