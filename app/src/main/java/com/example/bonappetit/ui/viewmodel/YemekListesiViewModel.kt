package com.example.bonappetit.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bonappetit.data.entity.Yemek
import com.example.bonappetit.data.repo.YemeklerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YemekListesiViewModel @Inject constructor(private var yemeklerRepository: YemeklerRepository): ViewModel() {
    var yemeklerListesi = MutableLiveData<List<Yemek>>()
    private val foodList = arrayOf("Izgara Somon", "Izgara Tavuk", "Köfte", "Lazanya", "Makarna", "Pizza")
    private val drinkList = arrayOf("Ayran", "Fanta", "Kahve", "Su")
    private val dessertList = arrayOf("Baklava", "Kadayıf", "Sütlaç", "Tiramisu")

    init {
        yemekleriYukle()
    }

    fun yemekleriYukle() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                yemeklerListesi.value = yemeklerRepository.yemekleriGetir()
            } catch (_: Exception){ }
        }
    }

    fun foodYemekleriGetir() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val yemekler = yemeklerRepository.yemekleriGetir()
                val filteredYemekler = mutableListOf<Yemek>()
                for(yemek in yemekler) {
                    if (yemek.yemek_adi in foodList) {
                        filteredYemekler.add(yemek)
                    }
                }
                yemeklerListesi.value = filteredYemekler
            } catch (_: Exception){ }
        }
    }

    fun drinkYemekleriGetir() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val yemekler = yemeklerRepository.yemekleriGetir()
                val filteredYemekler = mutableListOf<Yemek>()
                for(yemek in yemekler) {
                    if (yemek.yemek_adi in drinkList) {
                        filteredYemekler.add(yemek)
                    }
                }
                yemeklerListesi.value = filteredYemekler
            } catch (_: Exception){ }
        }
    }

    fun tatliYemekleriGetir() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val yemekler = yemeklerRepository.yemekleriGetir()
                val filteredYemekler = mutableListOf<Yemek>()
                for(yemek in yemekler) {
                    if (yemek.yemek_adi in dessertList) {
                        filteredYemekler.add(yemek)
                    }
                }
                yemeklerListesi.value = filteredYemekler
            } catch (_: Exception){ }
        }
    }

    fun yemekAra(aramaKelimesi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val yemekler = yemeklerRepository.yemekleriGetir()
            val filteredYemekler = mutableListOf<Yemek>()
            for(yemek in yemekler) {
                if (yemek.yemek_adi?.contains(aramaKelimesi, ignoreCase = true) == true) {
                    filteredYemekler.add(yemek)
                }
            }
            yemeklerListesi.value = filteredYemekler
        }
    }
}