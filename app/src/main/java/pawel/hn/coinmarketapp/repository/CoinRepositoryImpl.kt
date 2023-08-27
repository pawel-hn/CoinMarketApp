package pawel.hn.coinmarketapp.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.model.coinmarketcap.toDomain

import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val coinApi: CoinApi
) : CoinRepository {

    override suspend fun getCoinsListing(): List<Coin> =
        coinApi.getCoinsFromNetworkNew(1, 5, "USD").fold(
            onSuccess = { response ->
               response.toDomain()
            },
            onFailure = { throwable ->
                throw throwable
            }
        )


    override suspend fun getCoinsPaging(page: Int, pageSize: Int): Flow<List<Coin>> {
        return coinApi.getCoinsFromNetworkNew(page, pageSize, "USD").fold(
            onSuccess = { response ->
                flowOf(response.toDomain())
            },
            onFailure = { throwable ->
              throw throwable
            }
        )
    }
}