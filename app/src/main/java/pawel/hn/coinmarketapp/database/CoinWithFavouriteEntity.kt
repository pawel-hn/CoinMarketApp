package pawel.hn.coinmarketapp.database

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.RoomWarnings
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.util.LOGO_FILE_TYPE
import pawel.hn.coinmarketapp.util.LOGO_SIZE_PX
import pawel.hn.coinmarketapp.util.LOGO_URL
import pawel.hn.coinmarketapp.util.formatPriceChange

@Entity(tableName = "coins_with_favourite_table")
data class CoinWithFavouriteEntity(
    @Embedded val coinEntity: CoinEntity,
    val favourite: Boolean,
)

fun List<CoinWithFavouriteEntity>.toDomain() = this.map { it.toDomain() }

fun CoinWithFavouriteEntity.toDomain() = Coin(
    coinId = coinEntity.coinId,
    name = coinEntity.name,
    symbol = coinEntity.symbol,
    favourite = favourite,
    price = coinEntity.price,
    change24h = formatPriceChange(coinEntity.change24h),
    isChange24hUp = coinEntity.change24h > 0,
    change7d = formatPriceChange(coinEntity.change7d),
    isChange7dUp = coinEntity.change7d > 0,
    cmcRank = coinEntity.cmcRank,
    imageUri = Uri.parse(LOGO_URL).buildUpon()
        .appendPath(LOGO_SIZE_PX)
        .appendPath(coinEntity.coinId.toString() + LOGO_FILE_TYPE)
        .build()
)