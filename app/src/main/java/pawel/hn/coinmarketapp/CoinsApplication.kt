package pawel.hn.coinmarketapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.database.CoinDatabase

@HiltAndroidApp
class CoinsApplication : Application() {

     private val database: CoinDatabase by lazy {
        CoinDatabase.getDataBase(this)
    }
    val repository: Repository by lazy {
        Repository(database.coinDao)
    }




}