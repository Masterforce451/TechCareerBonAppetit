package com.example.bonappetit.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.bonappetit.R
import com.example.bonappetit.databinding.FragmentYemekSayfasiBinding
import com.example.bonappetit.preferences.AppPref
import com.example.bonappetit.ui.viewmodel.YemekSayfasiViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("PrivatePropertyName")
@AndroidEntryPoint
class YemekSayfasiFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentYemekSayfasiBinding
    private lateinit var viewModel: YemekSayfasiViewModel
    private var kullanici_adi: String = ""
    @SuppressLint("DiscouragedApi", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYemekSayfasiBinding.inflate(inflater, container, false)

        val bundle: YemekSayfasiFragmentArgs by navArgs()
        val yemek = bundle.yemek

        val appPref = AppPref(requireContext())
        CoroutineScope(Dispatchers.Main).launch {
            kullanici_adi =  appPref.kullaniciAdiAl()
            viewModel.sepettekiAdetiAl(yemek.yemek_adi!!, kullanici_adi)
            viewModel.ara(yemek.yemek_adi!!)
        }


        viewModel.sepettekiYemekAdeti.observe(viewLifecycleOwner) { adet ->
            if (adet > 0) {
                binding.textViewSepettekiAdet.text = "${getString(R.string.in_cart)} $adet"
            } else {
                binding.textViewSepettekiAdet.text = ""
            }
        }

        binding.textViewFoodAmount.text = "1"
        viewModel.sepeteEklenecekAdet.observe(viewLifecycleOwner) { adet ->
            if (adet > 1) {
                binding.buttonSubtractFood.isClickable = true
                binding.buttonSubtractFood.background = ContextCompat.getDrawable(requireContext(), R.drawable.food_page_button_background)
            } else {
                binding.buttonSubtractFood.isClickable = false
                binding.buttonSubtractFood.background = ContextCompat.getDrawable(requireContext(), R.drawable.food_button_background_zero_amount)
            }
            binding.textViewFoodAmount.text = adet.toString()
        }

        binding.textViewYemek.text = yemek.yemek_adi
        binding.textViewFiyat.text = "${yemek.yemek_fiyat} ₺"
        binding.textViewYemekAciklama.text = resources.getString(resources.getIdentifier(yemek.yemek_adi?.replace("\\s".toRegex(), ""), "string", requireContext().packageName))
        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${yemek.yemek_resim_adi}"
        Glide.with(requireContext()).load(url).override(400, 400).into(binding.imageViewYemek)

        binding.buttonSubtractFood.background = ContextCompat.getDrawable(requireContext(), R.drawable.food_button_background_zero_amount)

        binding.buttonSubtractFood.setOnClickListener {
            viewModel.eklenecekAzalt()
        }

        binding.buttonAddFood.setOnClickListener {
            viewModel.eklenecekArttir()
        }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.sepeteYemekEkle(yemek, kullanici_adi)
            Snackbar.make(it, "${binding.textViewFoodAmount.text} ${yemek.yemek_adi} ${getString(R.string.added_to_cart)}", Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.cancel)) {
                    viewModel.sepettenYemekSil(yemek.yemek_adi!!, binding.textViewFoodAmount.text.toString().toInt(), kullanici_adi)
                }.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        this@YemekSayfasiFragment.dismiss()
                    }
                }).show()
        }

        viewModel.favori.observe(viewLifecycleOwner) {
            if (it) {
                binding.favouriteButton.setImageResource(R.drawable.food_favourite_true)
            } else {
                binding.favouriteButton.setImageResource(R.drawable.food_favourite_false)
            }
        }

        binding.favouriteButton.setOnClickListener {
            if (binding.favouriteButton.drawable.toBitmap().sameAs(ResourcesCompat.getDrawable(resources, R.drawable.food_favourite_false, null)?.toBitmap())) {
                viewModel.favoriKaydet(yemek.yemek_adi!!)
                Snackbar.make(binding.favouriteButton, "${binding.textViewYemek.text} ${getString(R.string.add_to_fav)}", Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.favoriSil(yemek.yemek_adi!!)
                Snackbar.make(binding.favouriteButton, "${binding.textViewYemek.text} ${getString(R.string.delete_from_fav)}", Snackbar.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: YemekSayfasiViewModel by viewModels()
        viewModel = tempViewModel
    }
}