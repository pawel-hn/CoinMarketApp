package pawel.hn.coinmarketapp.usecase

import kotlinx.coroutines.flow.Flow
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.repository.CoinRepository
import javax.inject.Inject

class GetCoinsListingsUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) {

    suspend fun execute(): List<Coin> {
        return coinRepository.getCoinsListing()
    }

    suspend fun executePaging(page: Int, pageSize: Int): Flow<List<Coin>> =
        coinRepository.getCoinsPaging(page, pageSize)

}



