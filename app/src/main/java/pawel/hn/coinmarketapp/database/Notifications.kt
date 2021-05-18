package pawel.hn.coinmarketapp.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "notifications_table")
data class Notifications(
    @PrimaryKey val notifyId: String,
    val onOff: Boolean
) : Parcelable