package com.example.bonappetit.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bonappetit.R
import com.example.bonappetit.databinding.FragmentYemekListesiBinding
import com.example.bonappetit.ui.adapter.YemeklerAdapter
import com.example.bonappetit.ui.viewmodel.YemekListesiViewModel
import com.example.bonappetit.utils.gecis
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class YemekListesiFragment : Fragment() {
    private lateinit var binding: FragmentYemekListesiBinding
    private lateinit var viewModel: YemekListesiViewModel
    private lateinit var yemeklerAdapter: YemeklerAdapter
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYemekListesiBinding.inflate(inflater, container, false)

        binding.yemekListesiRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)


        binding.toggleButton.check(binding.buttonAllProducts.id)

        viewModel.yemeklerListesi.observe(viewLifecycleOwner) {
            yemeklerAdapter = YemeklerAdapter(requireContext(), it)
            binding.yemekListesiRecyclerView.adapter = yemeklerAdapter
        }



        binding.buttonAllProducts.setOnClickListener {
            viewModel.yemekleriYukle()
        }

        binding.buttonFood.setOnClickListener {
            viewModel.foodYemekleriGetir()
        }

        binding.buttonDrink.setOnClickListener {
            viewModel.drinkYemekleriGetir()
        }

        binding.buttonDessert.setOnClickListener {
            viewModel.tatliYemekleriGetir()
        }

        binding.searchViewYemek.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.yemekAra(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.yemekAra(newText)
                }
                return true
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: YemekListesiViewModel by viewModels()
        viewModel = tempViewModel
    }

    override fun onResume() {
        super.onResume()
        binding.toggleButton.check(binding.buttonAllProducts.id)
    }
}

