package pawel.hn.coinmarketapp.repository

import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseCoins
import retrofit2.Response

interface CoinRepository {


    suspend fun getCoinsListing(): Response<ApiResponseCoins>


}