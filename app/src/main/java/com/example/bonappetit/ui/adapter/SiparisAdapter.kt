package com.example.bonappetit.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bonappetit.R
import com.example.bonappetit.data.entity.SepetYemek
import com.example.bonappetit.databinding.SonucYemekKartTasarimBinding

class SiparisAdapter(var context: Context, private var yemekListesi: List<SepetYemek>):
    RecyclerView.Adapter<SiparisAdapter.CardTasarimTutucu>(){
    inner class CardTasarimTutucu(var tasarim: SonucYemekKartTasarimBinding):
        RecyclerView.ViewHolder(tasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val binding = SonucYemekKartTasarimBinding.inflate(LayoutInflater.from(context))
        return CardTasarimTutucu(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {
        val yemek = yemekListesi[position]
        val tasarim = holder.tasarim

        tasarim.foodName.text = yemek.yemek_adi
        tasarim.foodPrice.text = "${yemek.yemek_fiyat * yemek.yemek_siparis_adet} ₺"
        tasarim.foodAmount.text = "${yemek.yemek_siparis_adet} adet"

        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${yemek.yemek_resim_adi}"
        Glide.with(holder.itemView.context).load(url).override(30, 30).into(tasarim.foodPhoto)
        tasarim.foodPhoto.foreground = AppCompatResources.getDrawable(context, R.drawable.food_foreground)
    }

    override fun getItemCount(): Int {
        return yemekListesi.size
    }
}