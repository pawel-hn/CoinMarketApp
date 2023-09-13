package pawel.hn.coinmarketapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pawel.hn.coinmarketapp.util.DATABASE_NAME

@Database(entities = [CoinEntity::class, Wallet::class, Notifications::class, FavouriteCoinEntity::class], version = 1)
abstract class CoinDatabase : RoomDatabase() {
    abstract val coinDao: CoinDao
    abstract val walletDao: WalletDao
    abstract val favouriteCoinDao: FavouriteCoinDao

    companion object {
        @Volatile
         lateinit var INSTANCE: CoinDatabase

        fun getDataBase(context: Context): CoinDatabase {
            synchronized(this) {
                if(!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        CoinDatabase::class.java,
                        DATABASE_NAME).build()
                }
            }
            return INSTANCE
        }

    }




}

