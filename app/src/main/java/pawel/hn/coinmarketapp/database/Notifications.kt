package pawel.hn.coinmarketapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications_table")
data class Notifications(
    @PrimaryKey val notifyId: String,
    val onOff: Boolean
)