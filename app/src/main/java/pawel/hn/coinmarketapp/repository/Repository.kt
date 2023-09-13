package pawel.hn.coinmarketapp.repository

import pawel.hn.coinmarketapp.data.CoinsData
import pawel.hn.coinmarketapp.data.RemoteData
import pawel.hn.coinmarketapp.data.WalletData
import pawel.hn.coinmarketapp.database.CoinEntity
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseCoins
import pawel.hn.coinmarketapp.model.coinmarketcap.CoinResponse
import pawel.hn.coinmarketapp.util.*
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    val coins: CoinsData,
    val wallet: WalletData,
    private val remote: RemoteData
) {
    var responseError = true

    suspend fun getCoinsData(ccy: String) {
        try {
            val response = remote.getCoins(
                API_QUERY_START,
                API_QUERY_LIMIT,
                ccy
            )

            handleApiResponse(response, ccy)
        } catch (e: Exception) {
            responseError = true
            e.printStackTrace()
            showLog(" Repository exception " + e.message)
        }
    }

    private suspend fun handleApiResponse(response: Response<ApiResponseCoins>, currency: String){
        when(response.code()) {
            200 -> responseSuccess(response.body(), currency)
            else -> responseFail(response.code())
        }
    }

    private fun responseFail(responseCode: Int)  {
        responseError = true
        when(responseCode) {
             in 400..499 -> showLog("code: $responseCode, problem with request")
            in 500..599 -> showLog("code: $responseCode, problem with server response")
            else -> showLog("code: $responseCode, problem with server response")
        }
    }

    private suspend fun responseSuccess(response: ApiResponseCoins?, currency: String) {
        val list = mutableListOf<CoinEntity>()
        response?.let { coinResponse ->
            responseError = false
            coinResponse.coins.forEach {
                list.add(it.apiResponseConvertToCoin(currency))
            }


        }

    }

    suspend fun getLatestBitcoinPrice(): Double? {
        var newPrice: Double? = null
        try {
            val response = remote.getLatestBitcoinPrice(BITCOIN_ID)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    newPrice = apiResponse.coinResponse[1]!!.quote.USD.price
                    showLog("getBtc: $newPrice")
                }
            }
        } catch (e: Exception) {
            showLog("Exception btc price: ${e.message}")
        }

        return newPrice
    }


    suspend fun getBitcoinData(): CoinResponse? {
       var btc: CoinResponse? = null
        try {
            val response = remote.getLatestBitcoinPrice(BITCOIN_ID)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    btc = apiResponse.coinResponse[1]

                }
            }
        } catch (e: Exception) {
            showLog("Exception btc price: ${e.message}")
        }

        return  btc
    }
}