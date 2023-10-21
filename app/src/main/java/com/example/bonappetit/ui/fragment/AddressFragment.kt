package com.example.bonappetit.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bonappetit.R
import com.example.bonappetit.databinding.FragmentAddressBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class AddressFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentAddressBinding
    private lateinit var map: GoogleMap
    private lateinit var adres: String
    private var izinKontrol = 0
    private lateinit var location: LatLng
    private lateinit var currentLocation: LatLng
    private lateinit var locationTask: Task<Location>
    private lateinit var flpc: FusedLocationProviderClient
    private lateinit var marker: Marker
    private var markerHasBeenSet = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)

        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val database = Firebase.firestore

        flpc = LocationServices.getFusedLocationProviderClient(requireActivity())

        database.collection("users").document(user!!.email!!).get().addOnSuccessListener { document ->
            if (document != null) {
                binding.editTextAdres.setText(document.get("location").toString())
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.buttonAdresiAl.setOnClickListener {
            map.clear()
            izinKontrol = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            if (izinKontrol == PackageManager.PERMISSION_GRANTED) {
                if (markerHasBeenSet) {
                    adres = getCompleteAddressString(location.latitude, location.longitude)
                    currentLocation = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18F))
                    map.addMarker(MarkerOptions().position(currentLocation))
                    binding.editTextAdres.setText(adres)
                } else {
                    locationTask = flpc.lastLocation
                    locationTask.addOnSuccessListener {
                        if (it != null) {
                            adres = getCompleteAddressString(it.latitude, it.longitude)
                            currentLocation = LatLng(it.latitude, it.longitude)
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18F))
                            map.addMarker(MarkerOptions().position(currentLocation))
                            binding.editTextAdres.setText(adres)
                        }
                    }
                }
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            }
        }

        binding.buttonSetAddress.setOnClickListener {
            adres = binding.editTextAdres.text.toString()
            val data = hashMapOf("location" to adres)
            database.collection("users").document(user.email!!).set(data, SetOptions.merge())
            Snackbar.make(binding.buttonSetAddress, getString(R.string.address_has_been_set), Snackbar.LENGTH_SHORT).show()
        }

        return binding.root
    }

    @Suppress("DEPRECATION")
    private fun getCompleteAddressString(latitude: Double, longitude: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                strAdd = strAdd.trim()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strAdd
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val konum = LatLng(39.2743631,35.3254875)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(konum, 6F))

        map.setOnMapClickListener { point ->
            markerHasBeenSet = true
            map.clear()
            marker = map.addMarker(MarkerOptions().position(LatLng(point.latitude, point.longitude)))!!
            location = LatLng(point.latitude, point.longitude)
        }

        map.setOnMarkerClickListener {
            map.clear()
            markerHasBeenSet = false
            true
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            izinKontrol = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationTask = flpc.lastLocation
                locationTask.addOnSuccessListener {
                    if (it != null) {
                        adres = getCompleteAddressString(it.latitude, it.longitude)
                        currentLocation = LatLng(it.latitude, it.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18F))
                        map.addMarker(MarkerOptions().position(currentLocation))
                        binding.editTextAdres.setText(adres)
                    }
                }
                Toast.makeText(requireContext(), getString(R.string.location_perms_granted), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.location_perms_not_granted), Toast.LENGTH_SHORT).show()
            }
        }
    }
}