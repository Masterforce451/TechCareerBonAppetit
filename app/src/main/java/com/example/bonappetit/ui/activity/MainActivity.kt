package com.example.bonappetit.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.bonappetit.R
import com.example.bonappetit.databinding.ActivityMainBinding
import com.example.bonappetit.preferences.AppPref
import com.example.bonappetit.ui.fragment.YemekListesiFragmentDirections
import com.example.bonappetit.utils.gecis
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    @SuppressLint("RtlHardcoded", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.itemIconTintList = null

        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val database = Firebase.firestore
        auth = Firebase.auth
        val appPref = AppPref(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.navView, navController)

        binding.navView.setNavigationItemSelectedListener {menuItem ->
            when (menuItem.itemId) {
                R.id.yemekListesiFragment -> { navController.navigate(R.id.yemekListesiFragment) }
                R.id.addressFragment -> {navController.navigate(R.id.addressFragment)}
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        binding.toolbar.buttonCart.setOnClickListener {
            val gecis = YemekListesiFragmentDirections.sepetGecis()
            Navigation.gecis(binding.fragmentContainerView, gecis)
        }

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            binding.toolbar.buttonCart.isClickable = destination.id != R.id.sepetFragment
        }

        if (user != null) {
            database.collection("users").document(user.email!!).get().addOnCompleteListener {
                val documentSnapshot = it.result
                val name = documentSnapshot.get("name")
                val address = documentSnapshot.get("address")
                if (address != null) {
                    binding.toolbar.adresDescription.text = address.toString()
                }
                binding.toolbar.adresName.text = "${getString(R.string.hello)} ${name.toString()}"

                database.collection("users").document(user.email!!).get().addOnSuccessListener { document ->
                    if (document != null) {
                        binding.toolbar.adresDescription.text = document.get("location").toString()
                    }
                }

                database.collection("users").document(user.email!!).addSnapshotListener { snapshot, _ ->
                    val adres = snapshot?.get("location").toString()
                    binding.toolbar.adresDescription.text = adres
                    CoroutineScope(Dispatchers.Main).launch {
                        appPref.adresKaydet(adres)
                    }
                }

                val navView = binding.navView.getHeaderView(0)
                val textViewName: TextView = navView.findViewById(R.id.textViewName)
                val textViewFirstLetter: TextView = navView.findViewById(R.id.TextViewFirstLetter)
                textViewName.text = name.toString()
                textViewFirstLetter.text = name.toString()[0].toString()

                val menu = binding.navView.menu
                val menuCikisButonu = menu.findItem(R.id.startActivity)
                menuCikisButonu.setOnMenuItemClickListener {
                    Firebase.auth.signOut()
                    val intent = Intent(this, StartActivity::class.java)
                    startActivity(intent)
                    true
                }
            }
        }

        binding.toolbar.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText || v is SearchView) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}