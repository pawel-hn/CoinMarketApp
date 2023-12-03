package pawel.hn.coinmarketapp.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import pawel.hn.coinmarketapp.domain.WalletCoin
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.repository.WalletRepository

class ObserveWalletUseCase(
    private val coinRepository: CoinRepository,
    private val walletRepository: WalletRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun build(): Flow<List<WalletCoin>> {
        val coins = coinRepository.getCoins()
        return walletRepository.observeCoinsFromWallet()
            .mapLatest {
                it.map { walletEntity ->
                    val coin = coins.first { coin -> coin.coinId == walletEntity.coinId }
                    WalletCoin(
                        coin,
                        walletEntity.volume,
                        walletEntity.walletNo
                    )
                }
            }
    }
}