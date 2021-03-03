package pawel.hn.coinmarketapp.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

const val BASE_URL = "https://pro-api.coinmarketcap.com/"

interface CoinsApi {
    @Headers("X-CMC_PRO_API_KEY: 1498da2f-e373-4820-ac54-b986d62cf1fa", "Accept: application/json")
    @GET("v1/cryptocurrency/listings/latest")
    suspend fun getLatestQuotes(
            @Query("start") start: Int,
            @Query("limit") limit: Int,
            @Query("convert") convert: String
     ): CoinsModel
}
