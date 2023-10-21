package com.example.bonappetit.retrofit

class ApiUtils {
    companion object {
        private const val BASE_URL = "http://kasimadalan.pe.hu/yemekler/"

        fun getYemeklerDao(): YemeklerDao {
            return RetrofitClient.getClient(BASE_URL).create(YemeklerDao::class.java)
        }
    }
}