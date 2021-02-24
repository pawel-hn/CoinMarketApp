package pawel.hn.coinmarketapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Coin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Coin::class)
    suspend fun insert(coin: Coin)

    @Update
    suspend fun update(coin: Coin)

    @Query("SELECT * FROM coins_table WHERE name LIKE '%' || :searchQuery || '%'")
    fun getAllCoins(searchQuery: String): LiveData<List<Coin>>

    @Query("SELECT * FROM coins_table WHERE favourite = 1 AND name LIKE '%' || :searchQuery || '%'")
    fun getCheckedCoins(searchQuery: String): LiveData<List<Coin>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Wallet::class)
    suspend fun insertIntoWallet(coin: Wallet)

    @Query("SELECT * FROM wallet_table")
    fun getWallet(): LiveData<List<Wallet>>

    @Delete(entity = Wallet::class)
    fun deleteFromWallet(coin: Wallet)

}