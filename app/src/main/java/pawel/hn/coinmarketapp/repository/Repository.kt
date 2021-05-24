package pawel.hn.coinmarketapp.repository

import pawel.hn.coinmarketapp.data.CoinsData
import pawel.hn.coinmarketapp.data.RemoteData
import pawel.hn.coinmarketapp.data.WalletData
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseArray
import pawel.hn.coinmarketapp.util.*
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    val coins: CoinsData,
    val wallet: WalletData,
    private val remote: RemoteData
) {
    var responseError = true

    suspend fun refreshData(ccy: String) {
        showLog("repo refresh $ccy")
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


    private suspend fun handleApiResponse(response: Response<ApiResponseArray>, currency: String){
        showLog("response code: ${response.code()}")
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


    private suspend fun responseSuccess(response: ApiResponseArray?, currency: String) {
        val list = mutableListOf<Coin>()
        response?.let { coinResponse ->
            responseError = false
            coinResponse.data.forEach {
                list.add(it.apiResponseConvertToCoin(currency))
            }

            if (coins.coinsAll.value.isNullOrEmpty()) {
                coins.insertCoins(list)
            } else {
                coins.updateCoins(list)
            }
        }

    }

    suspend fun getLatestBitcoinPrice(): Double? {
        var newPrice: Double? = null
        try {
            val response = remote.getLatestBitcoinPrice(BITCOIN_ID)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    newPrice = apiResponse.data[1]!!.quote.USD.price
                }
            }
        } catch (e: Exception) {
            showLog("Exception btc price: ${e.message}")
        }

        return newPrice
    }

}