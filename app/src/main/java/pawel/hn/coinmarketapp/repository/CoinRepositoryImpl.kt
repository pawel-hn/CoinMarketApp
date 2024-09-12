package pawel.hn.coinmarketapp.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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
    private val favouriteCoinDao: FavouriteCoinDao,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : CoinRepository {

    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    private val _isLoading = MutableStateFlow(false)

    override val coins: StateFlow<CoinsNetworkState> = combine(
        _coins,
        _isLoading
    ) { data, loading ->
        CoinsNetworkState(
            data, loading
        )
    }.stateIn(
        scope, SharingStarted.WhileSubscribed(), CoinsNetworkState(
            emptyList(), true
        )
    )

    override suspend fun getCoinsPagingFromApi() {
        _isLoading.value = true
        coinApi.getCoinsFromNetworkNew(1, 100, "USD").fold(
            onSuccess = { response ->
                saveCoinsToDatabase(response.toEntity())
            },
            onFailure = { throwable ->
                _isLoading.value = false
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

    override suspend fun observeCoins(query: String, isFavourite: Boolean) {
        coinWithFavouriteDao.getCoinsWithFavourites(isFavourite, query)
            .collectLatest {

                _isLoading.value = false
                _coins.value = it.toDomain()
            }
    }


    override suspend fun getCoins(): List<Coin> =
        coinDao.getSavedCoins().map { it.toDomain() }
}


data class CoinsNetworkState(
    val coins: List<Coin>,
    val isLoading: Boolean
)