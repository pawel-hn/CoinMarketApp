package pawel.hn.coinmarketapp.usecase

import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.util.Resource
import javax.inject.Inject

class GetCoinsListingsUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) {

    suspend fun execute(): Resource<List<Coin>> {
       return coinRepository.getCoinsListing()
    }

}



