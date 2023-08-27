package pawel.hn.coinmarketapp.repository


import kotlinx.coroutines.flow.Flow
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.util.Resource

interface CoinRepository {

    suspend fun getCoinsListing(): List<Coin>

    suspend fun getCoinsPaging(page: Int, pageSize: Int): Flow<List<Coin>>

}