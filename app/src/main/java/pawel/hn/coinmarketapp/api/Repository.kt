package pawel.hn.coinmarketapp.api

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.CoinDao
import pawel.hn.coinmarketapp.toCoinsWithCheckBox

class Repository(private val coinDao: CoinDao) {

    private var coinList: LiveData<List<Coin>> = coinDao.getAllCoins("")


    suspend fun refreshData(start: Int, limit: Int, convert: String) {
        Log.d(TAG, "refreshData called")

        try {
            val response = CoinsApi.retrofitService.getLatestQuotes(start, limit, convert)
            val list = response.data.map {
                it.toCoinsWithCheckBox()
            }
            if (coinList.value == null) {

                withContext(Dispatchers.IO) {
                    coinDao.insertAll(list)
                    coinList = coinDao.getAllCoins("")
                    }
            } else {
                for (i in 0..list.lastIndex) {
                    val listTemp = coinList.value!!.filter {
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
        return if (isChecked) {
            coinDao.getCheckedCoins(searchQuery)
        } else {
            coinDao.getAllCoins(searchQuery)
        }
    }


}