package com.example.bonappetit.utils

import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.Navigation

fun Navigation.gecis(view: View, id: NavDirections) {
    findNavController(view).navigate(id)
}