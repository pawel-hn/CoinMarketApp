package pawel.hn.coinmarketapp.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Data class representing objects hold in database and presented to user via Wallet
 */
@Entity(tableName = "wallet_table")
@Parcelize
data class Wallet(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val coinId: Int,
    val name: String,
    val symbol: String,
    val volume: Double,
    val price: Double,
    val total: Double,
    val walletNo: Int
) : Parcelable




