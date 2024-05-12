package pawel.hn.coinmarketapp.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.database.CoinDao
import pawel.hn.coinmarketapp.database.CoinEntity
import pawel.hn.coinmarketapp.database.CoinWithFavouriteDao
import pawel.hn.coinmarketapp.database.FavouriteCoinDao
import pawel.hn.coinmarketapp.database.FavouriteCoinEntity
import pawel.hn.coinmarketapp.database.toDomain
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.model.coinmarketcap.toEntity
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val coinApi: CoinApi,
    private val coinDao: CoinDao,
    private val coinWithFavouriteDao: CoinWithFavouriteDao,
    private val favouriteCoinDao: FavouriteCoinDao
) : CoinRepository {

    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    override val coins: StateFlow<List<Coin>> = _coins.asStateFlow()

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

    override suspend fun observeCoins(query: String, isFavourite: Boolean): Flow<List<Coin>> =
        coinWithFavouriteDao.getCoinsWithFavourites(isFavourite,query).map {it.toDomain() }


    override suspend fun getCoins(): List<Coin> =
        coinDao.getSavedCoins().map { it.toDomain() }
}
