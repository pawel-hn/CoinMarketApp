package pawel.hn.coinmarketapp.data

import pawel.hn.coinmarketapp.database.CoinDao
import pawel.hn.coinmarketapp.database.Notifications
import javax.inject.Inject

class CoinsData @Inject constructor(private val coinDao: CoinDao) {

    val notifications = coinDao.getNotifications()

    suspend fun insertNotifications(notifications: Notifications) {
        coinDao.insertNotification(notifications)
    }

    suspend fun deleteNotification() {
        coinDao.deleteNotification()
    }
}