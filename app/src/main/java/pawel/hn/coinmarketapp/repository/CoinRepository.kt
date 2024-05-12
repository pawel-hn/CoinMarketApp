package pawel.hn.coinmarketapp.repository


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import pawel.hn.coinmarketapp.database.CoinEntity
import pawel.hn.coinmarketapp.domain.Coin

interface CoinRepository {

    val coins: StateFlow<List<Coin>>

    suspend fun getCoinsPagingFromApi()

    suspend fun saveCoinsToDatabase(coins: List<CoinEntity>)

    suspend fun saveFavouriteCoinId(id: Int)

    suspend fun deleteFavouriteCoinId(id: Int)

    suspend fun observeCoins(query: String, isFavourite: Boolean)

    suspend fun getCoins(): List<Coin>
}