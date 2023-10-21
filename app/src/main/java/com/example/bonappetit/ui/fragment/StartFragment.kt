package com.example.bonappetit.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.bonappetit.R
import com.example.bonappetit.databinding.FragmentStartBinding
import com.example.bonappetit.preferences.AppPref
import com.example.bonappetit.ui.activity.StartActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StartFragment : Fragment() {
    private lateinit var binding: FragmentStartBinding
    private lateinit var appPref: AppPref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        appPref = AppPref(requireContext())
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed(this::startIntro, 3000)

        return binding.root
    }

    private fun startIntro() {
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if (user != null && user.isEmailVerified) {
            val gecis = StartFragmentDirections.otomatikGecis()
            findNavController().navigate(gecis)
            (activity as StartActivity).finish()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                appPref.adresKaydet(getString(R.string.please_enter_address))
                val gecis = StartFragmentDirections.girisSayfasiGecis()
                findNavController().navigate(gecis)
            }
        }
    }
}