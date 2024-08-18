package pawel.hn.coinmarketapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "wallet_table")
data class WalletEntity(
    @PrimaryKey val coinId: Int,
    val volume: Double,
    val walletNo: Int
)