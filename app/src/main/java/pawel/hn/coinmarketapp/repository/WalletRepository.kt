package pawel.hn.coinmarketapp.repository


import kotlinx.coroutines.flow.Flow
import pawel.hn.coinmarketapp.database.WalletEntity
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.domain.WalletCoin

interface WalletRepository {

    suspend fun saveCoinToWallet(coin: Coin, volume: Double)

    suspend fun observeCoinsFromWallet(): Flow<List<WalletEntity>>

}