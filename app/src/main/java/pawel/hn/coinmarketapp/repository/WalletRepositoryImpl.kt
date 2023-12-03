package pawel.hn.coinmarketapp.repository

import kotlinx.coroutines.flow.Flow
import pawel.hn.coinmarketapp.database.WalletDao
import pawel.hn.coinmarketapp.database.WalletEntity
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.domain.toWalletDatabase

class WalletRepositoryImpl(
    private val walletDao: WalletDao
) : WalletRepository {

    override suspend fun saveCoinToWallet(coin: Coin, volume: Double) {
        walletDao.insert(coin.toWalletDatabase(volume))
    }

    override suspend fun observeCoinsFromWallet(): Flow<List<WalletEntity>> =
        walletDao.observeWallet()
}