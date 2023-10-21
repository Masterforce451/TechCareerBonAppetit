package com.example.bonappetit.di

import android.content.Context
import androidx.room.Room
import com.example.bonappetit.data.datasource.FavorilerDataSource
import com.example.bonappetit.data.datasource.YemeklerDataSource
import com.example.bonappetit.data.entity.Favoriler
import com.example.bonappetit.data.repo.FavorilerRepository
import com.example.bonappetit.data.repo.YemeklerRepository
import com.example.bonappetit.retrofit.ApiUtils
import com.example.bonappetit.retrofit.YemeklerDao
import com.example.bonappetit.room.FavorilerDao
import com.example.bonappetit.room.VeriTabani
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class)
class AppModule {
    @Provides @Singleton
    fun provideYemeklerRepository(yemeklerDataSource: YemeklerDataSource): YemeklerRepository {
        return YemeklerRepository(yemeklerDataSource)
    }

    @Provides @Singleton
    fun provideYemeklerDataSource(yemeklerDao: YemeklerDao): YemeklerDataSource {
        return YemeklerDataSource(yemeklerDao)
    }

    @Provides @Singleton
    fun provideYemeklerDao(): YemeklerDao {
        return ApiUtils.getYemeklerDao()
    }

    @Provides @Singleton
    fun provideFavorilerRepository(favorilerDataSource: FavorilerDataSource): FavorilerRepository {
        return FavorilerRepository(favorilerDataSource)
    }

    @Provides @Singleton
    fun provideFavorilerDataSource(favorilerDao: FavorilerDao): FavorilerDataSource {
        return FavorilerDataSource(favorilerDao)
    }

    @Provides @Singleton
    fun provideFavorilerDao(@ApplicationContext context: Context): FavorilerDao {
        val veriTabani = Room.databaseBuilder(context, VeriTabani::class.java, "favoriler.sqlite").createFromAsset("favoriler.sqlite").build()
        return veriTabani.getFavorilerDao()
    }
}