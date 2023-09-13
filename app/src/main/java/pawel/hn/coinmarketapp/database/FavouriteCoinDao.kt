package pawel.hn.coinmarketapp.database

import androidx.room.*

@Dao
interface FavouriteCoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = FavouriteCoinEntity::class)
    suspend fun saveFavourite(id: FavouriteCoinEntity)

    @Query("DELETE from favourite_coin WHERE id = :id")
    suspend fun deleteFavourite(id: Int)

    @Query("SELECT * from favourite_coin WHERE id = :id IS NOT NULL")
    suspend fun checkIfFavourite(id: Int): Boolean?
}