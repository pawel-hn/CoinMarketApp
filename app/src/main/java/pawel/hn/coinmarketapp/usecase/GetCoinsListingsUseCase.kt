package pawel.hn.coinmarketapp.usecase

import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseCoins
import pawel.hn.coinmarketapp.model.coinmarketcap.toDomain
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.repository.SafeApiCall

import pawel.hn.coinmarketapp.util.Resource
import javax.inject.Inject

class GetCoinsListingsUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) : SafeApiCall<ApiResponseCoins, List<Coin>>() {

    suspend operator fun invoke(): Resource<List<Coin>> =
        safeApiCall { coinRepository.getCoinsListing() }

    override fun convertResponseToDomainObject(data: ApiResponseCoins): List<Coin> = data.toDomain()
}



