package pawel.hn.coinmarketapp.repository

import androidx.lifecycle.LiveData
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.CoinDao
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.util.apiResponseConvertToCoin
import pawel.hn.coinmarketapp.util.numberUtil
import pawel.hn.coinmarketapp.util.showLog
import javax.inject.Inject

class Repository @Inject constructor(
    private val coinDao: CoinDao,
    private val coinApi: CoinApi
) : RepositoryInterface {

    override val coinsRepository = coinDao.getAllCoins("")
    override val coinListChecked = coinDao.getCheckedCoins("")
    override val walletRepository = coinDao.getWallet()
    override var responseError = true

    override suspend fun refreshData() {
        val list = mutableListOf<Coin>()

        try {
            val response = coinApi.getLatestQuotes(1, 100, "USD")

            if (response.isSuccessful) {
                response.body()?.let { coinResponse ->
                    responseError = false
                    coinResponse.data.forEach {
                        list.add(it.apiResponseConvertToCoin())
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
                responseError = true
                showLog(response.message())
            }

        } catch (e: Exception) {
            responseError = true
            e.printStackTrace()
            showLog("Exception " + e.message)
        }
    }

    override suspend fun updateCoin(coin: Coin, isChecked: Boolean) {
        coinDao.update(coin.copy(favourite = isChecked))
    }

    override fun getCoinsList(isChecked: Boolean, searchQuery: String): LiveData<List<Coin>> {
        synchronized(this) {
            return if (isChecked) {
                coinDao.getCheckedCoins(searchQuery)
            } else {
                coinDao.getAllCoins(searchQuery)
            }
        }
    }

    override fun createWalletCoin(coinName: String, coinVolume: Double): Wallet {
        val price = coinsRepository.value?.find { it.name == coinName }?.price ?: 0.0
        val total = price * coinVolume

        return Wallet(coinName, coinVolume, price, total)
    }

    override suspend fun addToWallet(coinWallet: Wallet) = coinDao.insertIntoWallet(coinWallet)

    override suspend fun deleteFromWallet(coin: Wallet) = coinDao.deleteFromWallet(coin)

    override suspend fun updateWallet(coin: Wallet, newPrice: Double) {
        val newTotal = coin.volume.toDouble() * newPrice

        coinDao.updateWallet(
            coin.copy(price = newPrice, total = newTotal
            )
        )
    }
}