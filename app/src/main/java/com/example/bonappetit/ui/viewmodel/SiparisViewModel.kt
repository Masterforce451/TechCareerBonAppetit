package com.example.bonappetit.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bonappetit.R
import com.example.bonappetit.data.entity.SepetYemek
import com.example.bonappetit.data.repo.YemeklerRepository
import com.example.bonappetit.preferences.AppPref
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.EOFException
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@Suppress("LocalVariableName")
@HiltViewModel
class SiparisViewModel @Inject constructor(private var yemeklerRepository: YemeklerRepository, @ApplicationContext private val context : Context): ViewModel() {
    var yemeklerListesi = MutableLiveData<List<SepetYemek>>()
    var yemekSiparisNumarasi = MutableLiveData<String>()
    var totalFiyat = MutableLiveData(0)
    private val appPref = AppPref(context)

    init {
        sepettekiYemekleriGetir()
    }

    private fun sepettekiYemekleriGetir() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                yemekSiparisNumarasi.value = "${context.getString(R.string.order_number)} "
                val kullanici_adi =  appPref.kullaniciAdiAl()
                totalFiyat.value = 0
                yemeklerListesi.value = yemeklerRepository.sepettekiYemekleriGetir(kullanici_adi)
                for (yemek in yemeklerListesi.value!!) {
                    totalFiyat.value = totalFiyat.value?.plus(yemek.yemek_siparis_adet * yemek.yemek_fiyat)
                    yemekSiparisNumarasi.value += yemek.sepet_yemek_id.toString() + yemek.yemek_siparis_adet.toString()
                }
            } catch (_: EOFException) {
                yemeklerListesi.value = emptyList()
            }
        }
    }

    fun sepetiTemizle(kullanici_adi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val yemekler = yemeklerRepository.sepettekiYemekleriGetir(kullanici_adi)
                if (yemekler.isNotEmpty()) {
                    for (yemek in yemekler) {
                        yemeklerRepository.sepettenYemekSil(yemek.sepet_yemek_id, kullanici_adi)
                    }
                }
            } catch (_: EOFException) { }
        }
    }
}