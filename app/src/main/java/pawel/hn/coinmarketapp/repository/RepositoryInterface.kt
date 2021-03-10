package pawel.hn.coinmarketapp.repository

import androidx.lifecycle.LiveData
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.Wallet

interface RepositoryInterface {

    val coinsRepository: LiveData<List<Coin>>
    val coinListChecked: LiveData<List<Coin>>

    suspend fun refreshData()

    suspend fun updateCoin(coin: Coin, isChecked: Boolean)

    fun getCoinsList(isChecked: Boolean, searchQuery: String): LiveData<List<Coin>>

    suspend fun addToWallet(coinName: String, coinVolume: String)

    suspend fun deleteFromWallet(coin: Wallet)

    suspend fun updateWallet(coin: Wallet, newPrice: Double)

    fun getWallet(): LiveData<List<Wallet>>

}