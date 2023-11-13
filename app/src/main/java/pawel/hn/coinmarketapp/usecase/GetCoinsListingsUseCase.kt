package pawel.hn.coinmarketapp.usecase

import kotlinx.coroutines.flow.Flow
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.repository.CoinRepository
import javax.inject.Inject

class GetCoinsListingsUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) {
    suspend fun execute(searchQuery: String): Flow<List<Coin>> =
        coinRepository.observeCoins(searchQuery)

}



