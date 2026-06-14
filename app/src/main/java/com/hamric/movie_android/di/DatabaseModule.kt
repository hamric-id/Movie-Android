package com.hamric.movie_android.di

import android.content.Context
import com.hamric.movie_android.data.local.FavoriteDao
import com.hamric.movie_android.data.local.MovieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase {
        return MovieDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: MovieDatabase): FavoriteDao {
        return database.favoriteDao()
    }
}