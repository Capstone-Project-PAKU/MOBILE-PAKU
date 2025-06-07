package com.example.paku.utils

import android.annotation.SuppressLint
import android.provider.Settings
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi

object DeviceUtils {
    fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun isFakeGpsCurrentlyUsed(context: Context, location: Location): Boolean {
        if (location.isFromMockProvider) return true
        if (location.isMock) return true

        val mockLocationEnabled = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.Secure.getInt(context.contentResolver, "mock_location", 0) != 0
            } else {
                val setting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ALLOW_MOCK_LOCATION)
                setting == "1" || setting.equals("true", ignoreCase = true)
            }
        } catch (e: Exception) {
            false
        }

        return mockLocationEnabled
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun checkInstalledMockApps(context: Context): Boolean {
        val knownMockApps = listOf(
            "com.lexa.fakegps",
            "com.incorporateapps.fakegps.fre",
            "com.blogspot.newapphorizons.fakegps",
            "com.hopefactory2021.fakegpslocation",
            "com.gsmartstudio.fakegps",
            "com.kajda.locations",
            "ru.gavrikov.mocklocations",
            "com.testfairy.app",
            "com.virtualgps.share",
            "com.mocklocation.gps",
            "com.fakegps.location",
            "com.app.gps.mockgps",
            "com.mockgps.spoof",
            "com.theappninjas.gpsjoystick",
            "com.pokemongoworld.pokemongomocklocations",
            "de.p72b.mocklocation",
            "com.mock.location.free",
            "com.android.development",
            "com.android.emulator",
            "com.genymobile.genymotion"
        )

        val packageManager = context.packageManager
        return knownMockApps.any { packageName ->
            try {
                packageManager.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }
}