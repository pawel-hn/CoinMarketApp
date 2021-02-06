package pawel.hn.coinmarketapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "coins_table")
data class Coin(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val coinId: Int,
    val name: String,
    val symbol: String,
    val favourite: Boolean,
    val price: Double,
    val change24h: Double
)


