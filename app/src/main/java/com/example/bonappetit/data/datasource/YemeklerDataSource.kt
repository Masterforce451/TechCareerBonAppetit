package com.example.bonappetit.data.datasource

import com.example.bonappetit.data.entity.SepetYemek
import com.example.bonappetit.data.entity.Yemek
import com.example.bonappetit.retrofit.YemeklerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("LocalVariableName")
class YemeklerDataSource(private var yemeklerDao: YemeklerDao) {
    suspend fun yemekleriGetir(): List<Yemek> = withContext(Dispatchers.IO) {
        return@withContext yemeklerDao.yemekleriGetir().yemekler
    }
    suspend fun sepettekiYemekleriGetir(kullanici_adi: String): List<SepetYemek> = withContext(Dispatchers.IO) {
        return@withContext yemeklerDao.sepettekiYemekleriGetir(kullanici_adi).sepet_yemekler
    }

    suspend fun sepeteYemekEkle(yemek_adi: String, yemek_resim_adi: String, yemek_fiyat: Int, yemek_siparis_adet: Int, kullanici_adi: String) {
        yemeklerDao.sepeteYemekEkle(yemek_adi, yemek_resim_adi, yemek_fiyat, yemek_siparis_adet, kullanici_adi)
    }

    suspend fun sepettenYemekSil(sepetYemekID: Int, kullaniciAdi: String) {
        yemeklerDao.sepettenYemekSil(sepetYemekID, kullaniciAdi)
    }
}