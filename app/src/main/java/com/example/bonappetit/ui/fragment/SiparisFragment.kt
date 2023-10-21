package com.example.bonappetit.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bonappetit.R
import com.example.bonappetit.databinding.FragmentSiparisBinding
import com.example.bonappetit.preferences.AppPref
import com.example.bonappetit.ui.adapter.SepetAdapter
import com.example.bonappetit.ui.adapter.SiparisAdapter
import com.example.bonappetit.ui.viewmodel.SepetViewModel
import com.example.bonappetit.ui.viewmodel.SiparisViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SiparisFragment : Fragment() {
    private lateinit var binding: FragmentSiparisBinding
    private lateinit var viewModel: SiparisViewModel
    private lateinit var yemeklerAdapter: SiparisAdapter
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSiparisBinding.inflate(inflater, container, false)

        binding.recyclerViewSiparisUrunler.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL,false)

        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val database = Firebase.firestore

        database.collection("users").document(user!!.email!!).get().addOnSuccessListener { document ->
            if (document != null) {
                binding.textViewAdres.text = document.get("location").toString()
                binding.textViewMessage.text = "${getString(R.string.thank_you_order)} ${document.get("name")}. ${getString(R.string.order_soon)}"
            }
        }

        viewModel.totalFiyat.observe(viewLifecycleOwner) { fiyat ->
            binding.textViewTotalFiyat.text = "$fiyat ₺"

        }

        viewModel.yemeklerListesi.observe(viewLifecycleOwner) {
            yemeklerAdapter = SiparisAdapter(requireContext(), it)
            binding.recyclerViewSiparisUrunler.adapter = yemeklerAdapter
        }

        viewModel.yemekSiparisNumarasi.observe(viewLifecycleOwner) {
            binding.textViewOrderNumber.text = it
        }

        viewModel.sepetiTemizle(user.email!!)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: SiparisViewModel by viewModels()
        viewModel = tempViewModel
    }

}