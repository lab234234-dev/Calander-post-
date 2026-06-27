package com.example.data

import kotlinx.coroutines.flow.Flow

class PosterRepository(private val posterDao: PosterDao) {
    
    val brandProfile: Flow<BrandProfile?> = posterDao.getBrandProfile()
    val favoritePosters: Flow<List<FavoritePoster>> = posterDao.getAllFavoritePosters()

    suspend fun saveBrandProfile(profile: BrandProfile) {
        posterDao.insertOrUpdateBrandProfile(profile)
    }

    fun isPosterFavorite(id: String): Flow<Boolean> {
        return posterDao.isPosterFavorite(id)
    }

    suspend fun addFavorite(poster: FavoritePoster) {
        posterDao.addPosterToFavorites(poster)
    }

    suspend fun removeFavorite(id: String) {
        posterDao.removePosterFromFavorites(id)
    }
}
