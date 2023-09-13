package pawel.hn.coinmarketapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = CoinEntity::class)
    suspend fun insertAll(list: List<CoinEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = CoinEntity::class)
    suspend fun insert(coinEntity: CoinEntity)

    @Update(entity = CoinEntity::class)
    suspend fun update(coinEntity: CoinEntity)

    @Query("SELECT * FROM coins_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY cmcRank ASC")
    fun getAllCoins(searchQuery: String): Flow<List<CoinEntity>>

    @Query("SELECT * FROM notifications_table")
    fun getNotifications(): LiveData<List<Notifications>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Notifications::class)
    suspend fun insertNotification(notifications: Notifications)

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Notifications::class)
    fun updateNotification(notifications: Notifications)

    @Query("DELETE FROM notifications_table")
    suspend fun deleteNotification()


}