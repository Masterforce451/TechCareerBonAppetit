package com.example.bonappetit.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bonappetit.R
import com.example.bonappetit.data.entity.Yemek
import com.example.bonappetit.databinding.YemekKartTasarimBinding
import com.example.bonappetit.ui.fragment.YemekListesiFragmentDirections
import com.example.bonappetit.utils.gecis

class YemeklerAdapter(var context: Context, private var yemekListesi: List<Yemek>):
    RecyclerView.Adapter<YemeklerAdapter.CardTasarimTutucu>(){
        inner class CardTasarimTutucu(var tasarim: YemekKartTasarimBinding):
                RecyclerView.ViewHolder(tasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val binding = YemekKartTasarimBinding.inflate(LayoutInflater.from(context), parent, false)
        return CardTasarimTutucu(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {
        val yemek = yemekListesi[position]
        val tasarim = holder.tasarim
        tasarim.foodName.text = yemek.yemek_adi
        tasarim.foodPrice.text = "${yemek.yemek_fiyat} ₺"

        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${yemek.yemek_resim_adi}"
        Glide.with(holder.itemView.context).load(url).override(150, 150).into(tasarim.foodPhoto)
        tasarim.foodPhoto.foreground = AppCompatResources.getDrawable(context, R.drawable.food_foreground)


        tasarim.yemekKart.setOnClickListener {
            val gecis = YemekListesiFragmentDirections.yemekSayfasiGecis(yemek = yemek)
            Navigation.gecis(it, gecis)
        }
    }

    override fun getItemCount(): Int {
        return yemekListesi.size
    }
}
