package pawel.hn.coinmarketapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.database.CoinDao
import pawel.hn.coinmarketapp.database.CoinEntity
import pawel.hn.coinmarketapp.database.FavouriteCoinDao
import pawel.hn.coinmarketapp.database.FavouriteCoinEntity
import pawel.hn.coinmarketapp.database.toDomain
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.model.coinmarketcap.toEntity
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val coinApi: CoinApi,
    private val coinDao: CoinDao,
    private val favouriteCoinDao: FavouriteCoinDao
) : CoinRepository {

    private val _state = MutableStateFlow(Result.success(emptyList<Coin>()))
    override val state: Flow<Result<List<Coin>>> = _state.asStateFlow()


    override suspend fun getCoinsPagingFromApi() {
        coinApi.getCoinsFromNetworkNew(1, 100, "USD").fold(
            onSuccess = { response ->
                saveCoinsToDatabase(response.toEntity())
            },
            onFailure = { throwable ->
                throwable.printStackTrace()
            }
        )
    }

    override suspend fun saveCoinsToDatabase(coins: List<CoinEntity>) {
        coinDao.insertAll(coins)
    }

    override suspend fun saveFavouriteCoinId(id: Int) =
        favouriteCoinDao.saveFavourite(FavouriteCoinEntity(id))

    override suspend fun deleteFavouriteCoinId(id: Int) =
        favouriteCoinDao.deleteFavourite(id)

    override suspend fun getFavourites(): Flow<List<Int>> =
        favouriteCoinDao.getFavourites().map { it.toDomain() }

    override suspend fun observeCoins(query: String) =
        coinDao.observeCoins(query).combine(getFavourites()) { coins, ids ->
            coins
                .toDomain()
                .map { coin -> coin.copy(favourite = ids.any { it == coin.coinId }) }
        }



    override suspend fun getCoins(): List<Coin> =
        coinDao.getSavedCoins().map { it.toDomain() }
}
