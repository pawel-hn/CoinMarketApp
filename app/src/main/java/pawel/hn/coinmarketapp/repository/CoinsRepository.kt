package pawel.hn.coinmarketapp.repository

import pawel.hn.coinmarketapp.data.CoinsData
import pawel.hn.coinmarketapp.data.RemoteData
import pawel.hn.coinmarketapp.data.WalletData
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseArray
import pawel.hn.coinmarketapp.model.coinmarketcap.CoinData
import pawel.hn.coinmarketapp.util.*
import javax.inject.Inject


class CoinsRepository @Inject constructor(
    val coins: CoinsData,
    val wallet: WalletData,
    private val remote: RemoteData
) {
    var responseError = true

    suspend fun getCoinsData(currency: String) {
        runCatching {
            remote.getCoins(
                API_QUERY_START,
                API_QUERY_LIMIT,
                currency
            )
        }.onSuccess {
            responseSuccess(it.body(), currency)
        }.onFailure {
            responseError = true
            showLog(" Repository exception " + it.message)
        }
    }


    private suspend fun responseSuccess(response: ApiResponseArray?, currency: String) {
        val list = mutableListOf<Coin>()
        response?.let { coinResponse ->
            responseError = false
            coinResponse.coinData.forEach {
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
        return getBitcoinData()?.quote?.USD?.price
    }

    suspend fun getBitcoinData(): CoinData? {
        var btc: CoinData? = null
        try {
            val response = remote.getLatestBitcoinPrice(BITCOIN_ID)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    btc = apiResponse.coinData[1]

                }
            }
        } catch (e: Exception) {
            showLog("Exception btc price: ${e.message}")
        }

        return btc
    }

}