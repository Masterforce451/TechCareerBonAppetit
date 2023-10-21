package com.example.bonappetit.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.bonappetit.databinding.FragmentRegisterBinding
import com.example.bonappetit.ui.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val name = binding.editTextName
        val email = binding.editTextEmail
        val password = binding.editTextPassword
        val passwordAgain = binding.editTextPasswordAgain

        binding.buttonRegister.setOnClickListener {
            viewModel.registerUser(name, email, password, passwordAgain)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: RegisterViewModel by viewModels()
        viewModel = tempViewModel
    }

}