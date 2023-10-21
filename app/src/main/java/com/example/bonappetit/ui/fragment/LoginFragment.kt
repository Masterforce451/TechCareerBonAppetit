package com.example.bonappetit.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.Navigation
import com.example.bonappetit.R
import com.example.bonappetit.databinding.FragmentLoginBinding
import com.example.bonappetit.preferences.AppPref
import com.example.bonappetit.ui.activity.StartActivity
import com.example.bonappetit.utils.gecis
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val signInCode = 123
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.buttonLogin.setOnClickListener {
            girisYap(binding.editTextEmail, binding.editTextPassword)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.revokeAccess()
        auth = FirebaseAuth.getInstance()

        binding.buttonGoogleLogin.setOnClickListener {
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, signInCode)
        }

        binding.buttonRegister.setOnClickListener {
            val gecis = LoginFragmentDirections.uyeOlmaSayfasiGecis()
            Navigation.gecis(it, gecis)
        }

        return binding.root
    }

    private fun girisYap(editTextEmail: EditText, editTextPassword: EditText) {
        val mAuth = FirebaseAuth.getInstance()

        if (editTextEmail.text.toString().isEmpty()) {
            editTextEmail.error = getString(R.string.enter_email)
            editTextEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text.toString()).matches()) {
            editTextEmail.error = getString(R.string.enter_email_valid)
            editTextEmail.requestFocus()
            return
        }

        if (editTextPassword.text.toString().isEmpty()) {
            editTextPassword.error = getString(R.string.password)
            editTextPassword.requestFocus()
            return
        }

        mAuth.signInWithEmailAndPassword(editTextEmail.text.toString(), editTextPassword.text.toString()).addOnCompleteListener {
            if (mAuth.currentUser?.isEmailVerified == false) {
                mAuth.currentUser?.sendEmailVerification()
                Snackbar.make(editTextEmail, getString(R.string.send_verification), Snackbar.LENGTH_LONG).show()
            } else {
                if (it.isSuccessful) {
                    val appPref = AppPref(requireContext())
                    CoroutineScope(Dispatchers.Main).launch {
                        appPref.kullaniciAdiKaydet(mAuth.currentUser?.email.toString())
                    }
                    val gecis = LoginFragmentDirections.girisBasariliGecis()
                    Navigation.gecis(binding.buttonLogin, gecis)
                    (activity as StartActivity).finish()
                } else {
                    Snackbar.make(binding.buttonLogin, getString(R.string.email_or_pw_wrong), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == signInCode) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                val idToken = account.getResult(ApiException::class.java).idToken
                firebaseAuthWithGoogle(idToken!!)
            } catch (e: ApiException) {
                Snackbar.make(binding.buttonGoogleLogin, getString(R.string.google_sign_in_failed), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val gecis = LoginFragmentDirections.girisBasariliGecis()
                    Navigation.gecis(binding.buttonGoogleLogin, gecis)
                    (activity as StartActivity).finish()
                } else {
                    Snackbar.make(binding.buttonGoogleLogin, getString(R.string.authentication_failed), Snackbar.LENGTH_SHORT).show()
                }
            }
    }


}

