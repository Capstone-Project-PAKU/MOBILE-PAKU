package com.example.paku.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import android.provider.Settings
import android.util.Log

object DeviceUtils {
    @SuppressLint("HardwareIds")
    fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isFakeGpsCurrentlyUsed(location: Location): Boolean {
        if (Build.VERSION.SDK_INT >= 31) {
            Log.d("fakujipiesu", ">= 31 ${location.isMock}")
            return location.isMock
        }
        Log.d("fakujipiesu", " other ${location.isFromMockProvider}")
        return location.isFromMockProvider

    }
}