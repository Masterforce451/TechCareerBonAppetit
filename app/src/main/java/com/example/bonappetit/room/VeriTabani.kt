package com.example.bonappetit.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bonappetit.data.entity.Favoriler

@Database(entities = [Favoriler::class], version = 1)
abstract class VeriTabani: RoomDatabase() {
    abstract fun getFavorilerDao(): FavorilerDao
}