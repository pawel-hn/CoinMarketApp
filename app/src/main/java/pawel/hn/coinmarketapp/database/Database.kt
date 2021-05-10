package pawel.hn.coinmarketapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Coin::class, Wallet::class, Notifications::class], version = 1)
abstract class CoinDatabase : RoomDatabase() {
    abstract val coinDao: CoinDao
    abstract val walletDao: WalletDao

    companion object {
        @Volatile
         lateinit var INSTANCE: CoinDatabase

        fun getDataBase(context: Context): CoinDatabase {
            synchronized(this) {
                if(!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        CoinDatabase::class.java,
                        "coins").build()
                }
            }
            return INSTANCE
        }

    }




}

