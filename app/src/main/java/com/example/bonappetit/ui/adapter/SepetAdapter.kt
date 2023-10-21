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
import com.example.bonappetit.databinding.SepetYemekKartTasarimBinding
import com.example.bonappetit.preferences.AppPref
import com.example.bonappetit.ui.viewmodel.SepetViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("LocalVariableName")
class SepetAdapter(var context: Context, private var yemekListesi: List<SepetYemek>, private var viewModel: SepetViewModel):
RecyclerView.Adapter<SepetAdapter.CardTasarimTutucu>(){
    inner class CardTasarimTutucu(var tasarim: SepetYemekKartTasarimBinding):
            RecyclerView.ViewHolder(tasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val binding = SepetYemekKartTasarimBinding.inflate(LayoutInflater.from(context))
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
        Glide.with(holder.itemView.context).load(url).override(60, 60).into(tasarim.foodPhoto)
        tasarim.foodPhoto.foreground = AppCompatResources.getDrawable(context, R.drawable.food_foreground)

        var kullanici_adi = ""
        val appPref = AppPref(context)
        CoroutineScope(Dispatchers.Main).launch {
            kullanici_adi =  appPref.kullaniciAdiAl()
        }

        tasarim.buttonDelete.setOnClickListener {
            viewModel.sepettenYemekSil(yemek.sepet_yemek_id, kullanici_adi)
            Snackbar.make(it, "${yemek.yemek_adi} ${context.getString(R.string.delete_from_cart)}", Snackbar.LENGTH_LONG).show()
        }

        tasarim.buttonAddOne.setOnClickListener {
            viewModel.sepeteBirEkle(yemek.sepet_yemek_id, kullanici_adi)
        }

        tasarim.buttonSubtractOne.setOnClickListener {
            viewModel.sepettenBirSil(yemek.sepet_yemek_id, kullanici_adi)
        }
    }

    override fun getItemCount(): Int {
        return yemekListesi.size
    }
}