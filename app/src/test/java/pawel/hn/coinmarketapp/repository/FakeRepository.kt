package pawel.hn.coinmarketapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.database.Wallet


class FakeRepository : RepositoryInterface {

    private val testCoin1 = Coin(1, "testCoin1", "T1",
        false, 1.0, 0.1)
    private val testCoin2 = Coin(2, "testCoin2", "T2",
        false, 0.5, 0.1)
    private val testCoin3 = Coin(3, "testCoin3", "T3",
        false, 2.0, 0.1)

    private val coinsList = mutableListOf(testCoin1, testCoin2, testCoin3)
    private val observableCoins = MutableLiveData<List<Coin>>(coinsList)

    override var responseError = false



    private val testWallet1 = Wallet("testCoin1", 4.0, 1.0, 10.0 )
    private val testWallet2 = Wallet("testCoin2", 1.0, 0.5, 5.0 )
    private val walletList = mutableListOf(testWallet1, testWallet2)
    private val observableWallet = MutableLiveData<List<Wallet>>(walletList)

    override val coinsRepository: LiveData<List<Coin>>
        get() = observableCoins

    override val coinListChecked: LiveData<List<Coin>>
        get() = observableCoins

    override val walletRepository: LiveData<List<Wallet>>
    get() = observableWallet


    override suspend fun refreshData() {
        coinsList.clear()
        coinsList.add(testCoin1)
        coinsList.add(testCoin2)
        observableCoins.postValue(coinsList)
    }

    override suspend fun updateCoin(coin: Coin, isChecked: Boolean) {
        val newCoin = coinsList.find { it.name == coin.name }
        coinsList.remove(coin)
        coinsList.add(newCoin!!.copy(favourite = isChecked))
    }

    override fun getCoinsList(isChecked: Boolean, searchQuery: String): LiveData<List<Coin>> {
        return observableCoins
    }

    override suspend fun addToWallet(coinWallet: Wallet) {
        walletList.add(coinWallet)
    }

    override fun createWalletCoin(coinName: String, coinVolume: Double): Wallet {
        val price = coinsRepository.value?.find { it.name == coinName }?.price ?: 0.0
        val total = price * coinVolume

        return Wallet(coinName, coinVolume, price, total)
    }

    override suspend fun deleteFromWallet(coin: Wallet) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWallet(coin: Wallet, newPrice: Double) {
        TODO("Not yet implemented")
    }

}