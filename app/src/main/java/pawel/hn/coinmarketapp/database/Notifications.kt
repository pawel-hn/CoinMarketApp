package pawel.hn.coinmarketapp.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Notifications data class, when notifications are on, instance of it is stored in database,
 * and is observed from NotifyFragment, after notification is sent, database is cleared and
 * switch turn to OFF in notify fragment.
 */
@Parcelize
@Entity(tableName = "notifications_table")
data class Notifications(
    @PrimaryKey val notifyId: String,
    val onOff: Boolean
) : Parcelable