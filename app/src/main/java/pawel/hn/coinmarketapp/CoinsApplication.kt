package pawel.hn.coinmarketapp

import android.app.Application
import pawel.hn.coinmarketapp.api.Repository
import pawel.hn.coinmarketapp.database.CoinDatabase

class CoinsApplication : Application() {

     private val database: CoinDatabase by lazy {
        CoinDatabase.getDataBase(this)
    }
    val repository: Repository by lazy {
        Repository(database.coinDao)
    }




}