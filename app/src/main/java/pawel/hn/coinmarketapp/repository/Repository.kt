package pawel.hn.coinmarketapp.repository

import pawel.hn.coinmarketapp.data.CoinsData
import pawel.hn.coinmarketapp.data.RemoteData
import pawel.hn.coinmarketapp.data.WalletData
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseArray
import pawel.hn.coinmarketapp.model.coinmarketcap.Data
import pawel.hn.coinmarketapp.util.*
import retrofit2.Response
import javax.inject.Inject

/**
 * Repository class, responsible for handling requests between viewModels and data.
 */
class Repository @Inject constructor(
    val coins: CoinsData,
    val wallet: WalletData,
    private val remote: RemoteData
) {
    var responseError = true


    /**
     * Getting new data from for all coins from CoinMarketCap.
     */
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


    /**
     * Whether response code is success or fail, passing on data further.
     */
    private suspend fun handleApiResponse(response: Response<ApiResponseArray>, currency: String){
        when(response.code()) {
            200 -> responseSuccess(response.body(), currency)
            else -> responseFail(response.code())
        }
    }

    /**
     * If code is one of errors, message is shows through logcat.
     */
    private fun responseFail(responseCode: Int)  {
        responseError = true
        when(responseCode) {
             in 400..499 -> showLog("code: $responseCode, problem with request")
            in 500..599 -> showLog("code: $responseCode, problem with server response")
            else -> showLog("code: $responseCode, problem with server response")
        }
    }

    /**
     * If response is success, fresh data is added to database. If database was not empty,
     * updateCoins() method makes sure that coins which were marked as favourite,
     * still are checked after refreshing data.
     */
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

    /**
     * Bitcoin latest price, used by NotifyWorker to check if price alert criteria are met,
     * also used by NotifyViewModel, so latest price is presented to user in NotifyFragment
     */
    suspend fun getLatestBitcoinPrice(): Double? {
        var newPrice: Double? = null
        try {
            val response = remote.getLatestBitcoinPrice(BITCOIN_ID)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    newPrice = apiResponse.data[1]!!.quote.USD.price
                    showLog("getBtc: $newPrice")
                }
            }
        } catch (e: Exception) {
            showLog("Exception btc price: ${e.message}")
        }

        return newPrice
    }

    /**
     * Bitcoin data for widget
     */
    suspend fun getBitcoinData(): Data? {
       var btc: Data? = null
        try {
            val response = remote.getLatestBitcoinPrice(BITCOIN_ID)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    btc = apiResponse.data[1]

                }
            }
        } catch (e: Exception) {
            showLog("Exception btc price: ${e.message}")
        }

        return  btc
    }

}