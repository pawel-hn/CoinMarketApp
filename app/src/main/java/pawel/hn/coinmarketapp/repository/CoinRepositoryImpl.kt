package pawel.hn.coinmarketapp.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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

    override suspend fun saveCoinsToDatabase(coins: List<CoinEntity>) {
        coinDao.insertAll(coins)
    }

    override suspend fun getCoinsFromDatabase(searchQuery: String): Flow<List<Coin>> =
        coinDao.getAllCoins(searchQuery).map { it.toDomain() }

    override suspend fun saveFavouriteCoinId(id: Int) =
        favouriteCoinDao.saveFavourite(FavouriteCoinEntity(id))

    override suspend fun deleteFavouriteCoinId(id: Int) =
        favouriteCoinDao.deleteFavourite(id)

    override suspend fun getFavourites(): Flow<List<Int>> =
        favouriteCoinDao.getFavourites().map { it.toDomain() }

}