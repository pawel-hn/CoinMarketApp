package pawel.hn.coinmarketapp.data

import androidx.lifecycle.LiveData
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.CoinDao
import pawel.hn.coinmarketapp.database.Notifications
import javax.inject.Inject

/**
 * CLass to maintain interactions with list of coins and notifications in database.
 */
class CoinsData @Inject constructor(private val coinDao: CoinDao) {

    /**
     * Get cons from database based on a search query. Allows for dynamic search on the coins fragment.
     * List of coins is observed in the fragment using livedata
     */
    fun getCoinsList(searchQuery: String, favourites: Boolean): LiveData<List<Coin>>
    = if (favourites) coinDao.getCheckedCoins(searchQuery) else  coinDao.getAllCoins(searchQuery)

    val coinsAll = coinDao.getAllCoins("")
    val coinsFavourite = coinDao.getCheckedCoins("")

    suspend fun insertCoins(coins: List<Coin>) = coinDao.insertAll(coins)

    suspend fun update(coin: Coin, isChecked: Boolean) {
        coinDao.update(coin.copy(favourite = isChecked))
    }

    suspend fun updateCoins(newList: List<Coin>) {
        val oldList = coinsAll.value!!
        for (i in 0..newList.lastIndex) {
            val oldCoin = oldList.find {
                it.coinId == newList[i].coinId
            }
            oldCoin?.let { _oldCoin ->
                update(newList[i], _oldCoin.favourite)
            }
        }
    }

    /**
     * Notifications are inserted into database and observed with livedata, so app knows when to
     * be ready to send a notification.
     */
    val notifications = coinDao.getNotifications()

    suspend fun insertNotifications(notifications: Notifications) {
        coinDao.insertNotification(notifications)
    }

    /**
     * Delete function is called after price alert criteria are met and notification was sent,
     * then switch in NotifyFragment is automatically turn to off.
     */
    suspend fun deleteNotification() {
        coinDao.deleteNotification()
    }



}