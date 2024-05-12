package pawel.hn.coinmarketapp.repository

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
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

    @OptIn(FlowPreview::class)
    override suspend fun observeCoins(query: String, isFavourite: Boolean) {
        coinWithFavouriteDao.getCoinsWithFavourites(isFavourite, query)
            .debounce(200)
            .collectLatest {
                _coins.value = it.toDomain()
            }
    }

    override suspend fun getCoins(): List<Coin> =
        coinDao.getSavedCoins().map { it.toDomain() }
}
