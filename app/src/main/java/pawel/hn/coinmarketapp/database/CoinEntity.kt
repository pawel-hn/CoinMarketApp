package pawel.hn.coinmarketapp.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.util.*

@Entity(tableName = "coins_table")
data class CoinEntity(
    @PrimaryKey val coinId: Int,
    val name: String,
    val symbol: String,
    val price: Double,
    val change24h: Double,
    val change7d: Double,
    val cmcRank: Int
)

fun CoinEntity.toDomain(isFavourite: Boolean = false) = Coin(
    coinId = this.coinId,
    name = this.name,
    symbol = this.symbol,
    favourite = isFavourite,
    price = this.price,
    change24h = formatPriceChange(this.change24h),
    isChange24hUp = this.change24h > 0,
    change7d = formatPriceChange(this.change7d),
    isChange7dUp = this.change7d > 0,
    cmcRank = this.cmcRank,
    imageUri = Uri.parse(LOGO_URL).buildUpon()
        .appendPath(LOGO_SIZE_PX)
        .appendPath(this.coinId.toString() + LOGO_FILE_TYPE)
        .build()
)


fun List<CoinEntity>.toDomain() = map { it.toDomain() }