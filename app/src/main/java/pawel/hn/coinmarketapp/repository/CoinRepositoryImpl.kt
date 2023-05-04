package pawel.hn.coinmarketapp.repository

import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseCoins
import retrofit2.Response

class CoinRepositoryImpl(
    private val coinApi: CoinApi
):CoinRepository {

    override suspend fun getCoinsListing(): Response<ApiResponseCoins> =
        coinApi.getCoinsFromNetwork(1,10, "USD")



}
