package com.example.bonappetit.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Patterns
import android.widget.EditText
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.bonappetit.R
import com.example.bonappetit.ui.fragment.RegisterFragmentDirections
import com.example.bonappetit.utils.gecis
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class RegisterViewModel @Inject constructor(@ApplicationContext private val context: Context): ViewModel() {
    fun registerUser(nameEditText: EditText, emailEditText: EditText, passwordEditText: EditText, passwordAgainEditText: EditText) {
        if (nameEditText.text.toString().trim().isEmpty()) {
            nameEditText.error = context.getString(R.string.enter_email)
            nameEditText.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()) {
            emailEditText.error = context.getString(R.string.enter_email_valid)
            emailEditText.requestFocus()
            return
        }

        if (passwordEditText.text.toString().isEmpty()) {
            passwordEditText.error = context.getString(R.string.password)
            passwordEditText.requestFocus()
            return
        }

        if (passwordEditText.text.toString().length < 8) {
            passwordEditText.error = context.getString(R.string.password_short)
            passwordEditText.requestFocus()
            return
        }

        if (passwordEditText.text.toString() != passwordAgainEditText.text.toString()) {
            passwordAgainEditText.error = context.getString(R.string.passwords_not_same)
            passwordAgainEditText.requestFocus()
            return
        }

        val mAuth = FirebaseAuth.getInstance()

        mAuth.createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                val locationPlaceholder = context.getString(R.string.please_enter_address)
                val language = Locale.getDefault().displayLanguage
                if (language == "English") {
                    mAuth.setLanguageCode(language)
                } else if (language == "Turkish") {
                    mAuth.setLanguageCode(language)
                }

                mAuth.currentUser?.sendEmailVerification()
                Snackbar.make(emailEditText, context.getString(R.string.sign_in_successful), Snackbar.LENGTH_LONG).show()
                val data = hashMapOf(
                    "name" to nameEditText.text.toString(),
                    "location" to locationPlaceholder
                )

                val database = Firebase.firestore
                val userID = mAuth.currentUser?.email
                if (userID != null) {
                    database.collection("users").document(userID).set(data)
                }

                val gecis = RegisterFragmentDirections.girisSayfasiDonus()
                Navigation.gecis(emailEditText, gecis)

            } else {
                if (it.exception is FirebaseAuthUserCollisionException) {
                    Snackbar.make(emailEditText, context.getString(R.string.email_in_use), Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}