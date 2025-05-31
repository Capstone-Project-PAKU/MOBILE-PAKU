package com.example.paku.utils

import android.provider.Settings
import android.content.Context

object DeviceUtils {
    fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isMockLocationEnabled(context: Context): Boolean {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ALLOW_MOCK_LOCATION) != "0"
    }
}