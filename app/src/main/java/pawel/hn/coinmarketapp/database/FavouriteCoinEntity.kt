package pawel.hn.coinmarketapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_coin")
data class FavouriteCoinEntity(
    @PrimaryKey val id: Int
)

fun List<FavouriteCoinEntity>.toDomain() = map { it.id }