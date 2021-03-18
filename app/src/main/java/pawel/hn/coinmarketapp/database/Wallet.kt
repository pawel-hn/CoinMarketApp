package pawel.hn.coinmarketapp.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "wallet_table")
@Parcelize
data class Wallet(
    @PrimaryKey val name: String,
    val volume: Double,
    val price: Double,
    val total: Double
): Parcelable