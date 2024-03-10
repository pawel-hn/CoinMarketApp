package pawel.hn.coinmarketapp.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = WalletEntity::class)
    suspend fun insert(coin: WalletEntity)

    @Query("SELECT * FROM wallet_table")
    fun observeWallet(): Flow<List<WalletEntity>>

    @Delete(entity = WalletEntity::class)
    suspend fun deleteFromWallet(coin: WalletEntity)

    @Query("DELETE FROM wallet_table")
    suspend fun deleteAllFromWallets()

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = WalletEntity::class)
    suspend fun updateWallet(coin: WalletEntity)
}