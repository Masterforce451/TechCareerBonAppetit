package com.example.bonappetit.data.entity

import java.io.Serializable

@Suppress("PropertyName")
data class Yemek(
    var yemek_id: Int,
    var yemek_adi: String?,
    var yemek_resim_adi: String?,
    var yemek_fiyat: Int): Serializable