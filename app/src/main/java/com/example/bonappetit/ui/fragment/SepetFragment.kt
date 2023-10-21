package com.example.bonappetit.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bonappetit.databinding.FragmentSepetBinding
import com.example.bonappetit.preferences.AppPref
import com.example.bonappetit.ui.adapter.SepetAdapter
import com.example.bonappetit.ui.viewmodel.SepetViewModel
import com.example.bonappetit.utils.gecis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Suppress("PrivatePropertyName")
@AndroidEntryPoint
class SepetFragment : Fragment() {
    private lateinit var binding: FragmentSepetBinding
    private lateinit var viewModel: SepetViewModel
    private lateinit var yemeklerAdapter: SepetAdapter
    private var kullanici_adi: String = ""
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSepetBinding.inflate(inflater, container, false)

        binding.recyclerViewSepet.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL,false)

        val appPref = AppPref(requireContext())
        CoroutineScope(Dispatchers.Main).launch {
            kullanici_adi =  appPref.kullaniciAdiAl()
        }

        viewModel.yemeklerListesi.observe(viewLifecycleOwner) {
            yemeklerAdapter = SepetAdapter(requireContext(), it, viewModel)
            binding.recyclerViewSepet.adapter = yemeklerAdapter
        }

        viewModel.totalFiyat.observe(viewLifecycleOwner) { fiyat ->
            binding.textViewTotalFiyat.text = "$fiyat ₺"
        }

        viewModel.siparisVerildi.observe(viewLifecycleOwner) { siparis ->
            if (siparis) {
                val gecis = SepetFragmentDirections.siparisGecis()
                Navigation.gecis(binding.buttonSiparisVer, gecis)
            }
        }


        binding.buttonSepetiTemizle.setOnClickListener {
            viewModel.sepetiTemizle(kullanici_adi, binding.buttonSepetiTemizle)
        }

        binding.buttonSiparisVer.setOnClickListener {
            viewModel.siparisVer(binding.buttonSiparisVer)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: SepetViewModel by viewModels()
        viewModel = tempViewModel
    }
}