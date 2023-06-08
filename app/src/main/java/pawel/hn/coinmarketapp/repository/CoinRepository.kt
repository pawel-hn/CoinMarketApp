package pawel.hn.coinmarketapp.repository

import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.util.Resource

interface CoinRepository {

    suspend fun getCoinsListing(): List<Coin>


}