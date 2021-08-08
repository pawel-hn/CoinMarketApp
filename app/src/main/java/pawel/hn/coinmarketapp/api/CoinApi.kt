package pawel.hn.coinmarketapp.api

import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponse
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseArray
import pawel.hn.coinmarketapp.util.API_HEADER
import pawel.hn.coinmarketapp.util.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CoinApi {

    @Headers("$API_HEADER $API_KEY", "Accept: application/json")
    @GET("v1/cryptocurrency/listings/latest")
    suspend fun getCoinsFromNetwork(
            @Query("start") start: Int,
            @Query("limit") limit: Int,
            @Query("convert") convert: String
     ): Response<ApiResponseArray>


    @Headers("$API_HEADER $API_KEY", "Accept: application/json")
    @GET("v1/cryptocurrency/quotes/latest")
    suspend fun getLatestSingleQuote(
        @Query("id") id: String
    ): Response<ApiResponse>


}
