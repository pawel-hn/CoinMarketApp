package pawel.hn.coinmarketapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Coin::class)
    suspend fun insertAll(list: List<Coin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Coin::class)
    suspend fun insert(coin: Coin)

    @Update(entity = Coin::class)
    suspend fun update(coin: Coin)

    @Query("SELECT * FROM coins_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY cmcRank ASC")
    fun getAllCoins(searchQuery: String): LiveData<List<Coin>>

    @Query("SELECT * FROM coins_table WHERE favourite = 1 AND name LIKE '%' || :searchQuery || '%'")
    fun getCheckedCoins(searchQuery: String): LiveData<List<Coin>>

    @Query("SELECT * FROM notifications_table")
    fun getNotifications(): LiveData<List<Notifications>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Notifications::class)
    suspend fun insertNotification(notifications: Notifications)

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Notifications::class)
    fun updateNotification(notifications: Notifications)

    @Query("DELETE FROM notifications_table")
    suspend fun deleteNotification()


}