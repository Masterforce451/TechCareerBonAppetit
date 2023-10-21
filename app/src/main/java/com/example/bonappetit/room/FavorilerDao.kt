package com.example.bonappetit.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bonappetit.data.entity.Favoriler

@Suppress("LocalVariableName") @Dao
interface FavorilerDao {
    @Insert
    suspend fun kaydet(yemek: Favoriler)

    @Query("DELETE FROM favoriler WHERE yemek_adi = :yemek_adi")
    suspend fun sil(yemek_adi: String)

    @Query("SELECT * FROM favoriler WHERE yemek_adi = :aranan_yemek")
    suspend fun ara(aranan_yemek: String): List<Favoriler>
}