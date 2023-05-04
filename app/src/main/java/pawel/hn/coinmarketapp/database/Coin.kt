package pawel.hn.coinmarketapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

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