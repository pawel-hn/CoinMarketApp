package pawel.hn.coinmarketapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.api.CoinsApi
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.CoinDao
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.toCoinsWithCheckBox
import javax.inject.Inject


class Repository @Inject constructor (
    private val coinDao: CoinDao,
    private val coinsApi: CoinsApi) {

    val coinsRepository = coinDao.getAllCoins("")
    val coinListChecked = coinDao.getCheckedCoins("")
    val walletRepository = coinDao.getWallet()

    suspend fun refreshData() {
        Log.d(TAG, "refreshData called coinlist")

        try {
            val response = coinsApi.getLatestQuotes(1, 100, "USD")
            val list = response.data.map {
                it.toCoinsWithCheckBox()
            }

            if (coinsRepository.value.isNullOrEmpty()) {
                Log.d(TAG, "base not init")
                withContext(Dispatchers.IO) {
                    coinDao.insertAll(list)
                }
            } else {
                Log.d(TAG, "base init")
                for (i in 0..list.lastIndex) {
                    val listTemp = coinsRepository.value!!.filter {
                        it.coinId == list[i].coinId
                    }
                    coinDao.update(list[i].copy(favourite = listTemp[0].favourite))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Exception: " + e.message)
        }
    }

    suspend fun update(coin: Coin, isChecked: Boolean) {
        coinDao.update(coin.copy(favourite = isChecked))
    }

    fun coinsList(isChecked: Boolean, searchQuery: String): LiveData<List<Coin>> {
        Log.d(TAG, " repository coinsList called")
        synchronized(this) {
            return if (isChecked) {
                coinDao.getCheckedCoins(searchQuery)
            } else {
                coinDao.getAllCoins(searchQuery)
            }
        }
    }

    suspend fun insertIntoWallet(coin: Wallet) = coinDao.insertIntoWallet(coin)

    suspend fun deleteFromWallet(coin: Wallet) = coinDao.deleteFromWallet(coin)

    suspend fun updateWallet(coin: Wallet) = coinDao.updateWallet(coin.copy())



}