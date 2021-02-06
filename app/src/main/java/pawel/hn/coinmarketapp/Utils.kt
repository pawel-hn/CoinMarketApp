package pawel.hn.coinmarketapp

import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.database.Coin

fun CoinApi.Data.toCoinsWithCheckBox() = Coin(
    coinId = this.id,
    name = this.name,
    symbol =this.symbol,
    favourite = false,
    price =  this.quote.USD.price,
    change24h =  this.quote.USD.percent_change_24h
)