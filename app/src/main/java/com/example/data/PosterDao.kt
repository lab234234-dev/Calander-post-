package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PosterDao {
    // Brand Profile operations (always id = 1)
    @Query("SELECT * FROM brand_profile WHERE id = 1 LIMIT 1")
    fun getBrandProfile(): Flow<BrandProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBrandProfile(profile: BrandProfile)

    // Favorite Poster operations
    @Query("SELECT * FROM favorite_posters ORDER BY timestamp DESC")
    fun getAllFavoritePosters(): Flow<List<FavoritePoster>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_posters WHERE id = :id)")
    fun isPosterFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPosterToFavorites(poster: FavoritePoster)

    @Query("DELETE FROM favorite_posters WHERE id = :id")
    suspend fun removePosterFromFavorites(id: String)
}
