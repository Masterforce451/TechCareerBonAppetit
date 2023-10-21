package com.example.bonappetit.data.repo

import com.example.bonappetit.data.datasource.YemeklerDataSource
import com.example.bonappetit.data.entity.Yemek

@Suppress("LocalVariableName")
class YemeklerRepository(private var yemeklerDataSource: YemeklerDataSource) {
    suspend fun yemekleriGetir(): List<Yemek> = yemeklerDataSource.yemekleriGetir()
    suspend fun sepettekiYemekleriGetir(kullanici_adi: String) = yemeklerDataSource.sepettekiYemekleriGetir(kullanici_adi)
    suspend fun sepeteYemekEkle(yemek_adi: String,
                                yemek_resim_adi: String,
                                yemek_Fiyat: Int,
                                yemek_siparis_adet: Int,
                                kullanici_adi: String) = yemeklerDataSource.sepeteYemekEkle(yemek_adi, yemek_resim_adi, yemek_Fiyat, yemek_siparis_adet, kullanici_adi)
    suspend fun sepettenYemekSil(sepet_yemek_id: Int, kullanici_adi: String) = yemeklerDataSource.sepettenYemekSil(sepet_yemek_id, kullanici_adi)
}