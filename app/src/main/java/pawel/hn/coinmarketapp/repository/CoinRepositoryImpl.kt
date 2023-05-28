package pawel.hn.coinmarketapp.repository

import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseCoins
import pawel.hn.coinmarketapp.model.coinmarketcap.ObjectMapper
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.handleApi

import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val coinApi: CoinApi,
    private val mapper: ObjectMapper<ApiResponseCoins, List<Coin>>,
) : CoinRepository {

    override suspend fun getCoinsListing(): Resource<List<Coin>> = handleApi(mapper) {
            coinApi.getCoinsFromNetworkNew(1, 8, "USD")
        }
}