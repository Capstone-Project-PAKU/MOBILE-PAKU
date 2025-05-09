package com.example.paku.data.api

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        RetrofitClient.init(applicationContext)
    }
}