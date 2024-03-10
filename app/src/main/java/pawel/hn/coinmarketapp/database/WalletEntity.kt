package pawel.hn.coinmarketapp.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "wallet_table")
@Parcelize
data class WalletEntity(
    @PrimaryKey val coinId: Int,
    val volume: Double,
    val walletNo: Int
) : Parcelable