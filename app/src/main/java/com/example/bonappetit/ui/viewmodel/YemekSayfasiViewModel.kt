package com.example.bonappetit.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bonappetit.data.entity.Yemek
import com.example.bonappetit.data.repo.FavorilerRepository
import com.example.bonappetit.data.repo.YemeklerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.EOFException
import javax.inject.Inject

@Suppress("LocalVariableName")
@HiltViewModel
class YemekSayfasiViewModel @Inject constructor(private var yemeklerRepository: YemeklerRepository, private var favorilerRepository: FavorilerRepository) : ViewModel() {
    val sepettekiYemekAdeti = MutableLiveData<Int>()
    val favori = MutableLiveData<Boolean>()
    val sepeteEklenecekAdet = MutableLiveData<Int>()

    init {
        sepeteEklenecekAdet.value = 1
    }

    fun sepeteYemekEkle(yemek: Yemek, kullanici_adi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                var found = false
                val yemekler = yemeklerRepository.sepettekiYemekleriGetir(kullanici_adi)
                for (gelen_yemek in yemekler) {
                    if (gelen_yemek.yemek_adi == yemek.yemek_adi) {
                        val yeni_adet = gelen_yemek.yemek_siparis_adet + sepeteEklenecekAdet.value!!
                        yemeklerRepository.sepettenYemekSil(gelen_yemek.sepet_yemek_id, kullanici_adi)
                        yemeklerRepository.sepeteYemekEkle(yemek.yemek_adi!!, yemek.yemek_resim_adi!!, yemek.yemek_fiyat, yeni_adet, kullanici_adi)
                        sepettekiYemekAdeti.value = yeni_adet
                        found = true
                    }
                }
                if (!found) {
                    yemeklerRepository.sepeteYemekEkle(yemek.yemek_adi!!, yemek.yemek_resim_adi!!, yemek.yemek_fiyat, sepeteEklenecekAdet.value!!, kullanici_adi)
                    sepettekiYemekAdeti.value = sepeteEklenecekAdet.value
                }

            } catch (e: EOFException) {
                yemeklerRepository.sepeteYemekEkle(yemek.yemek_adi!!, yemek.yemek_resim_adi!!, yemek.yemek_fiyat, sepeteEklenecekAdet.value!!, kullanici_adi)
                sepettekiYemekAdeti.value = sepeteEklenecekAdet.value
            }
        }
    }

    fun sepettenYemekSil(yemekAd: String, silinecek_yemek_adeti: Int, kullanici_adi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val yemekler = yemeklerRepository.sepettekiYemekleriGetir(kullanici_adi)
            for (yemek in yemekler) {
                if (yemekAd == yemek.yemek_adi) {
                    val yeni_yemek_adeti = yemek.yemek_siparis_adet - silinecek_yemek_adeti
                    sepettekiYemekAdeti.value = yeni_yemek_adeti
                    yemeklerRepository.sepettenYemekSil(yemek.sepet_yemek_id, kullanici_adi)
                    yemek.yemek_adi.let {
                        yemek.yemek_resim_adi?.let {
                                yemek_resim_adi -> yemeklerRepository.sepeteYemekEkle(it, yemek_resim_adi, yemek.yemek_fiyat, yeni_yemek_adeti, kullanici_adi)
                        }
                    }
                }
            }
        }
    }

    fun sepettekiAdetiAl(yemekAd: String, kullanici_adi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val yemekler = yemeklerRepository.sepettekiYemekleriGetir(kullanici_adi)
                for (yemek in yemekler) {
                    if (yemekAd == yemek.yemek_adi) {
                        sepettekiYemekAdeti.value = yemek.yemek_siparis_adet
                    }
                }
            } catch (_: EOFException) { }
            catch (_: NullPointerException) {
                sepettekiYemekAdeti.value = 0
            }
        }
    }

    fun favoriKaydet(yemek_adi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            favorilerRepository.kaydet(yemek_adi)
            favori.value = true
        }
    }

    fun favoriSil(yemek_adi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            favorilerRepository.sil(yemek_adi)
            favori.value = false
        }
    }

    fun ara(aranan_yemek: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val favoriYemek = favorilerRepository.ara(aranan_yemek)
            favori.value = favoriYemek.isNotEmpty()
        }
    }

    fun eklenecekArttir() {
        CoroutineScope(Dispatchers.Main).launch {
            sepeteEklenecekAdet.value = sepeteEklenecekAdet.value?.inc()
        }
    }

    fun eklenecekAzalt() {
        CoroutineScope(Dispatchers.Main).launch {
            sepeteEklenecekAdet.value = sepeteEklenecekAdet.value?.dec()
        }
    }
}
