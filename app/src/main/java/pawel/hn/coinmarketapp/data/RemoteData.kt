package pawel.hn.coinmarketapp.data

import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponse
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseCoins
import pawel.hn.coinmarketapp.util.showLogN
import retrofit2.Response
import javax.inject.Inject

class RemoteData @Inject constructor(private val coinApi: CoinApi) {

    suspend fun getCoins(start: Int, limit: Int, ccy: String): Response<ApiResponseCoins>{
        showLogN("$start, $limit, $ccy")
        return coinApi.getCoinsFromNetwork(start, limit, ccy)
    }

    suspend fun getLatestBitcoinPrice(id: String): Response<ApiResponse>
    = coinApi.getLatestSingleQuote(id)

}