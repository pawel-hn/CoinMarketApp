package pawel.hn.coinmarketapp.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.database.*
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.model.coinmarketcap.toEntity
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val coinApi: CoinApi,
    private val coinDao: CoinDao,
    private val favouriteCoinDao: FavouriteCoinDao
) : CoinRepository {

    override suspend fun getCoinsPagingFromApi() {
        coinApi.getCoinsFromNetworkNew(1, 100, "USD").fold(
            onSuccess = { response -> saveCoinsToDatabase(response.toEntity()) },
            onFailure = { throwable -> throw throwable }
        )
    }

    override suspend fun saveCoinsToDatabase(coins: List<CoinEntity>) = coinDao.insertAll(coins)


    override suspend fun getCoinsFromDatabase(searchQuery: String): Flow<List<Coin>> =
        coinDao.getAllCoins(searchQuery).map { it.toDomain() }

    override suspend fun saveFavouriteCoinId(id: Int) =
        favouriteCoinDao.saveFavourite(FavouriteCoinEntity(id))

    override suspend fun deleteFavouriteCoinId(id: Int) =
        favouriteCoinDao.deleteFavourite(id)

    override suspend fun getFavourites(): Flow<List<Int>> =
        favouriteCoinDao.getFavourites().map { it.toDomain() }

    override suspend fun observeCoins(): Flow<List<Coin>> =
        withContext(Dispatchers.IO) {
            coinDao.getAllCoins("").combine(getFavourites()) { coins, ids ->
                coins
                    .toDomain()
                    .map { coin -> coin.copy(favourite = ids.any { it == coin.coinId }) }
            }
        }
}