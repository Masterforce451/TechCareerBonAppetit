package com.example.bonappetit.data.repo

import com.example.bonappetit.data.datasource.FavorilerDataSource

class FavorilerRepository(var favorilerDataSource: FavorilerDataSource) {
    suspend fun kaydet(yemek_adi: String) = favorilerDataSource.kaydet(yemek_adi)
    suspend fun sil(yemek_adi: String) = favorilerDataSource.sil(yemek_adi)
    suspend fun ara(aranan_yemek: String) = favorilerDataSource.ara(aranan_yemek)
}