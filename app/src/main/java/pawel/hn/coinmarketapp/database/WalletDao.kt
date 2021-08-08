package pawel.hn.coinmarketapp.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Wallet::class)
    suspend fun insertIntoWallet(coin: Wallet)

    @Query("SELECT * FROM wallet_table ORDER BY total DESC")
    fun getWallet(): LiveData<List<Wallet>>

    @Delete(entity = Wallet::class)
    suspend fun deleteFromWallet(coin: Wallet)

    @Query("DELETE FROM wallet_table")
    suspend fun deleteAllFromWallets()

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Wallet::class)
    suspend fun updateWallet(coin: Wallet)

}