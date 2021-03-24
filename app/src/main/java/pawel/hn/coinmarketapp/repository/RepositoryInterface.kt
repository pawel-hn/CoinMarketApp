package pawel.hn.coinmarketapp.repository

import androidx.lifecycle.LiveData
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.Wallet

interface RepositoryInterface {

    val coinsRepository: LiveData<List<Coin>>
    val coinListChecked: LiveData<List<Coin>>
    val walletRepository: LiveData<List<Wallet>>
    var responseError: Boolean

    suspend fun getSingleQuote(coinIds: String)

    suspend fun refreshData()

    suspend fun updateCoin(coin: Coin, isChecked: Boolean)

    fun getCoinsList(isChecked: Boolean, searchQuery: String): LiveData<List<Coin>>

    suspend fun addToWallet(coinWallet: Wallet)

    suspend fun deleteFromWallet(coin: Wallet)

    suspend fun updateWallet(coin: Wallet, newPrice: Double)

    fun createWalletCoin (coinName: String, coinVolume: Double): Wallet

}