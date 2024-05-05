package pawel.hn.coinmarketapp.usecase

import pawel.hn.coinmarketapp.repository.CoinRepository
import javax.inject.Inject

class GetCoinsListingsUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) {


}



