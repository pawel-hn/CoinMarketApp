package pawel.hn.coinmarketapp.repository

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
}