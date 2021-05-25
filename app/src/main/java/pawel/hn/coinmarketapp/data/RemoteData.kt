package pawel.hn.coinmarketapp.data

import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponse
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseArray
import retrofit2.Response
import javax.inject.Inject

/**
 * Class responsible for getting data from Internet.
 */
class RemoteData @Inject constructor(private val coinApi: CoinApi) {

    suspend fun getCoins(start: Int, limit: Int, ccy: String): Response<ApiResponseArray>
    = coinApi.getCoinsFromNetwork(start, limit, ccy)

    suspend fun getLatestBitcoinPrice(id: String): Response<ApiResponse>
    = coinApi.getLatestSingleQuote(id)

}