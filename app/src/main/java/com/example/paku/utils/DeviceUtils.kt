package com.example.paku.utils

import android.provider.Settings
import android.content.Context

object DeviceUtils {
    public fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}