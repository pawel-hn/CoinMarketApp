package pawel.hn.coinmarketapp.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinWithFavouriteDao {
    @Query("""SELECT coins_table.*,
        favourite_coin.id IS NOT NULL AS favourite FROM coins_table
        LEFT JOIN favourite_coin ON coins_table.coinId = favourite_coin.id
        WHERE (:onlyFavourites = 1 AND favourite = 1) OR (:onlyFavourites = 0)
        AND name LIKE '%' || :searchQuery || '%' 
        ORDER BY cmcRank ASC 
        """)
    fun getCoinsWithFavourites(onlyFavourites:Boolean, searchQuery: String): Flow<List<CoinWithFavouriteEntity>>
}