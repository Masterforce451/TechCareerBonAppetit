package com.example.bonappetit.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.datastore: DataStore<Preferences> by preferencesDataStore("bilgiler")
@Suppress("LocalVariableName")
class AppPref(var context: Context) {
    companion object {
        val kullanici_adi = stringPreferencesKey("kullanici_adi")
        val kullanici_adres = stringPreferencesKey("kullanici_adres")
    }

    suspend fun kullaniciAdiKaydet(yeni_kullanici_adi: String) {
        context.datastore.edit {
            it[kullanici_adi] = yeni_kullanici_adi
        }
    }

    suspend fun kullaniciAdiAl(): String {
        val preferences = context.datastore.data.first()
        return preferences[kullanici_adi]!!
    }

    suspend fun adresKaydet(yeni_adres: String) {
        context.datastore.edit {
            it[kullanici_adres] = yeni_adres
        }
    }

    suspend fun kullaniciAdresiAl(): String {
        val preferences = context.datastore.data.first()
        return preferences[kullanici_adres]!!
    }
}