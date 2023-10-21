package com.example.bonappetit.data.datasource

import com.example.bonappetit.data.entity.Favoriler
import com.example.bonappetit.room.FavorilerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("LocalVariableName")
class FavorilerDataSource(var favorilerDao: FavorilerDao) {
    suspend fun kaydet(yemek_adi: String) {
        val yeni_yemek = Favoriler(0, yemek_adi)
        favorilerDao.kaydet(yeni_yemek)
    }

    suspend fun sil(yemek_adi: String) {
        favorilerDao.sil(yemek_adi)
    }

    suspend fun ara(aranan_yemek: String): List<Favoriler> = withContext(Dispatchers.IO) {
        return@withContext favorilerDao.ara(aranan_yemek)
    }
}