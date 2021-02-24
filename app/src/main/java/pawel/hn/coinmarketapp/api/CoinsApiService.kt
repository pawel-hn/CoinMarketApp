package pawel.hn.coinmarketapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

const val BASE_URL = "https://pro-api.coinmarketcap.com/"

interface CoinsApiService {
    @Headers("X-CMC_PRO_API_KEY: 1498da2f-e373-4820-ac54-b986d62cf1fa", "Accept: application/json")
    @GET("v1/cryptocurrency/listings/latest")
    suspend fun getLatestQuotes(
            @Query("start") start: Int,
            @Query("limit") limit: Int,
            @Query("convert") convert: String
     ): CoinApi
}


private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

object CoinsApi {
    val retrofitService: CoinsApiService by lazy {
        retrofit.create(CoinsApiService::class.java)
    }
}


//?start=1&limit=5000&convert=USD