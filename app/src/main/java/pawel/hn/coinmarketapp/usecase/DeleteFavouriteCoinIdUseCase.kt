package pawel.hn.coinmarketapp.usecase

import pawel.hn.coinmarketapp.repository.CoinRepository
import javax.inject.Inject

class DeleteFavouriteCoinIdUseCase @Inject constructor(
    private val coinRepository: CoinRepository
){
    suspend fun execute(id:Int) = coinRepository.deleteFavouriteCoinId(id)
}