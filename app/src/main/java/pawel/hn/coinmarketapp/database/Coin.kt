package pawel.hn.coinmarketapp.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import pawel.hn.coinmarketapp.util.*
import pawel.hn.coinmarketapp.viewmodels.CoinForView

@Entity(tableName = "coins_table")
data class Coin(
    @PrimaryKey val coinId: Int,
    val name: String,
    val symbol: String,
    val favourite: Boolean,
    val price: Double,
    val change24h: Double,
    val change7d: Double,
    val cmcRank: Int
)

fun Coin.toPresentation() = CoinForView(
    coinId = this.coinId,
    name = this.name,
    symbol = this.symbol,
    favourite = false,
    price = formatPriceAndVolForView(this.price, ValueType.Fiat, CURRENCY_USD).toString(),
    change24h = formatPriceChange(this.change24h),
    change7d = formatPriceChange(this.change7d),
    cmcRank = this.cmcRank,
    imageUri = Uri.parse(LOGO_URL).buildUpon()
        .appendPath(LOGO_SIZE_PX)
        .appendPath(this.coinId.toString() + LOGO_FILE_TYPE)
        .build()
)