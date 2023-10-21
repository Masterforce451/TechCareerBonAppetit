package com.example.bonappetit.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bonappetit.R
import com.example.bonappetit.data.entity.SepetYemek
import com.example.bonappetit.data.repo.YemeklerRepository
import com.example.bonappetit.preferences.AppPref
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
class SepetViewModel @Inject constructor(private var yemeklerRepository: YemeklerRepository, @ApplicationContext private val context : Context): ViewModel() {
    var yemeklerListesi = MutableLiveData<List<SepetYemek>>()
    var totalFiyat = MutableLiveData(0)
    val siparisVerildi = MutableLiveData<Boolean>()
    private val appPref = AppPref(context)
    private val mAuth = FirebaseAuth.getInstance()
    private val user = mAuth.currentUser
    private val database = Firebase.firestore

    init {
        sepettekiYemekleriGetir()
    }

    private fun sepettekiYemekleriGetir() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val kullanici_adi =  appPref.kullaniciAdiAl()
                totalFiyat.value = 0
                yemeklerListesi.value = yemeklerRepository.sepettekiYemekleriGetir(kullanici_adi)
                for (yemek in yemeklerListesi.value!!) {
                    totalFiyat.value = totalFiyat.value?.plus(yemek.yemek_siparis_adet * yemek.yemek_fiyat)
                }
            } catch (_: EOFException) {
                yemeklerListesi.value = emptyList()
            }
        }
    }

    fun sepetiTemizle(kullanici_adi: String, buttonSepetiTemizle: MaterialButton) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val yemekler = yemeklerRepository.sepettekiYemekleriGetir(kullanici_adi)
                if (yemekler.isNotEmpty()) {
                    for (yemek in yemekler) {
                        yemeklerRepository.sepettenYemekSil(yemek.sepet_yemek_id, kullanici_adi)
                    }
                }
                totalFiyat.value = 0
                yemeklerListesi.value = emptyList()
                Snackbar.make(buttonSepetiTemizle, context.getString(R.string.clear_cart), Snackbar.LENGTH_SHORT).show()
            } catch (_: EOFException) { }
        }
    }

    fun sepettenYemekSil(sepet_yemek_id: Int, kullanici_adi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val yemekler = yemeklerRepository.sepettekiYemekleriGetir(kullanici_adi)
            for (yemek in yemekler) {
                if (sepet_yemek_id == yemek.sepet_yemek_id) {
                    totalFiyat.value = totalFiyat.value?.minus(yemek.yemek_siparis_adet * yemek.yemek_fiyat)
                    yemeklerRepository.sepettenYemekSil(sepet_yemek_id, kullanici_adi)
                }
            }
            sepettekiYemekleriGetir()
        }
    }

    fun siparisVer(buttonSiparisVer: MaterialButton) {
        CoroutineScope(Dispatchers.Main).launch {
            var currentAddress = appPref.kullaniciAdresiAl()
            database.collection("users").document(user?.email!!).get().addOnSuccessListener { document ->
                if (document != null) {
                    currentAddress = document.get("location").toString()
                }
            }
            if (yemeklerListesi.value?.isEmpty() == false) {
                if (currentAddress == "" || currentAddress == "Please enter your address." || currentAddress == "Lütfen adresinizi giriniz.") {
                    Snackbar.make(buttonSiparisVer, context.getString(R.string.address_empty), Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(buttonSiparisVer, context.getString(R.string.order_placed), Snackbar.LENGTH_SHORT).show()
                    siparisVerildi.value = true
                }
            } else {
                if (yemeklerListesi.value?.isEmpty() == true) {
                    Snackbar.make(buttonSiparisVer, context.getString(R.string.cart_is_empty), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sepeteBirEkle(sepet_yemek_id: Int, kullanici_adi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val yemekler = yemeklerRepository.sepettekiYemekleriGetir(kullanici_adi)
            for (yemek in yemekler) {
                if(sepet_yemek_id == yemek.sepet_yemek_id) {
                    val yeni_adet = yemek.yemek_siparis_adet + 1
                    yemeklerRepository.sepettenYemekSil(yemek.sepet_yemek_id, kullanici_adi)
                    yemeklerRepository.sepeteYemekEkle(yemek.yemek_adi!!, yemek.yemek_resim_adi!!, yemek.yemek_fiyat, yeni_adet, kullanici_adi)
                }
            }
            sepettekiYemekleriGetir()
        }
    }

    fun sepettenBirSil(sepet_yemek_id: Int, kullanici_adi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val yemekler = yemeklerRepository.sepettekiYemekleriGetir(kullanici_adi)
            for (yemek in yemekler) {
                if(sepet_yemek_id == yemek.sepet_yemek_id) {
                    if (yemek.yemek_siparis_adet > 1) {
                        val yeni_adet = yemek.yemek_siparis_adet - 1
                        yemeklerRepository.sepettenYemekSil(yemek.sepet_yemek_id, kullanici_adi)
                        yemeklerRepository.sepeteYemekEkle(yemek.yemek_adi!!, yemek.yemek_resim_adi!!, yemek.yemek_fiyat, yeni_adet, kullanici_adi)
                    } else {
                        yemeklerRepository.sepettenYemekSil(yemek.sepet_yemek_id, kullanici_adi)
                    }
                }
            }
            sepettekiYemekleriGetir()
        }
    }
}