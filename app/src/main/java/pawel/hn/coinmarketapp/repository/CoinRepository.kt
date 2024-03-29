package pawel.hn.coinmarketapp.repository


import kotlinx.coroutines.flow.Flow
import pawel.hn.coinmarketapp.database.CoinEntity
import pawel.hn.coinmarketapp.domain.Coin

interface CoinRepository {

    suspend fun getCoinsPagingFromApi()

    suspend fun saveCoinsToDatabase(coins: List<CoinEntity>)

    suspend fun saveFavouriteCoinId(id: Int)

    suspend fun deleteFavouriteCoinId(id: Int)

    suspend fun getFavourites(): Flow<List<Int>>

    suspend fun observeCoins(query: String): Flow<List<Coin>>

    suspend fun getCoins(): List<Coin>
}