package com.atech.core.module

import android.content.Context
import androidx.room.Room
import com.atech.core.api.ApiCacheDatabase
import com.atech.core.api.syllabus.SyllabusCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiCacheDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ApiCacheDatabase =
        Room.databaseBuilder(context, ApiCacheDatabase::class.java, ApiCacheDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideDao(database: ApiCacheDatabase): SyllabusCacheDao = database.syllabusCacheDao()
}