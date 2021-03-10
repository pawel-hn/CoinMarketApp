package pawel.hn.coinmarketapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pawel.hn.coinmarketapp.*
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.CoinDao
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.formatter
import javax.inject.Inject

class Repository @Inject constructor(
    private val coinDao: CoinDao,
    private val coinApi: CoinApi
) : RepositoryInterface {

    override val coinsRepository = coinDao.getAllCoins("")
    override val coinListChecked = coinDao.getCheckedCoins("")
    val walletRepository = coinDao.getWallet()

    override suspend fun refreshData() {
        Log.d(TAG, "repository refreshData called")
        val list = mutableListOf<Coin>()

        try {
            val response = coinApi.getLatestQuotes(1, 100, "USD")

            if (response.isSuccessful) {
                response.body()?.let { coinResponse ->
                    coinResponse.data.forEach {
                        list.add(it.toCoinsWithCheckBox())
                    }

                    if (coinsRepository.value.isNullOrEmpty()) {
                            coinDao.insertAll(list)
                    } else {
                        val listTemp = coinsRepository.value!!
                        for (i in 0..list.lastIndex) {
                            val listLoop = listTemp.filter {
                                it.coinId == list[i].coinId
                            }
                            if (listLoop.isNotEmpty()) {
                                coinDao.update(list[i].copy(favourite = listLoop[0].favourite))
                            }
                        }
                    }
                }
            } else {
                showLog(response.message())
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showLog("Exception " + e.message)
        }
    }

    override suspend fun updateCoin(coin: Coin, isChecked: Boolean) {
        coinDao.update(coin.copy(favourite = isChecked))
    }

    override fun getCoinsList(isChecked: Boolean, searchQuery: String): LiveData<List<Coin>> {
        Log.d(TAG, " repository getCoinsList called")
        synchronized(this) {
            return if (isChecked) {
                coinDao.getCheckedCoins(searchQuery)
            } else {
                coinDao.getAllCoins(searchQuery)
            }
        }
    }

    override suspend fun addToWallet(coinName: String, coinVolume: String) {
        val price = coinsRepository.value?.find { it.name == coinName }?.price ?: 0
        val total = formatterTotal.format(price.toDouble() * coinVolume.toDouble())
        val priceFormat = formatter.format(price)
        val coin =
            Wallet(coinName, formatterVolume.format(coinVolume.toDouble()), priceFormat, total)
        Log.d(TAG, coin.toString())
        coinDao.insertIntoWallet(coin)
    }

    override suspend fun deleteFromWallet(coin: Wallet) = coinDao.deleteFromWallet(coin)

    override suspend fun updateWallet(coin: Wallet, newPrice: Double) {
        val newTotal =
            coin.volume.replace(",", "").toDouble() * newPrice
        coinDao.updateWallet(
            coin.copy(
                price = formatter.format(newPrice),
                total = formatterTotal.format(newTotal)
            )
        )
    }

    override fun getWallet(): LiveData<List<Wallet>> = coinDao.getWallet()
}