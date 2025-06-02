package com.example.paku.utils
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import java.io.File

class ComprehensiveMockLocationDetector(private val context: Context) {

    companion object {
        private const val TAG = "MockLocationDetector"
    }

    /**
     * Comprehensive mock location detection using multiple methods
     */
    fun isMockLocationEnabled(): MockLocationResult {
        val detectionMethods = mutableMapOf<String, Boolean>()

        // Method 1: Check system settings (works for basic apps)
        detectionMethods["system_setting"] = checkSystemSetting()

        // Method 2: Check for installed mock location apps
        detectionMethods["installed_apps"] = checkInstalledMockApps()

        // Method 3: Check for root access (advanced spoofing often requires root)
        detectionMethods["root_access"] = checkRootAccess()

        // Method 4: Check for Xposed framework (commonly used for spoofing)
        detectionMethods["xposed_framework"] = checkXposedFramework()

        // Method 5: Check developer options status
        detectionMethods["developer_options"] = checkDeveloperOptions()

        // Method 6: Check for debugging flags
        detectionMethods["debug_flags"] = checkDebuggingFlags()

        val positiveDetections = detectionMethods.values.count { it }
        val totalMethods = detectionMethods.size

        return MockLocationResult(
            isMockDetected = positiveDetections > 0,
            confidenceLevel = calculateConfidence(detectionMethods),
            detectionMethods = detectionMethods,
            riskLevel = when {
                positiveDetections >= 3 -> RiskLevel.HIGH
                positiveDetections >= 2 -> RiskLevel.MEDIUM
                positiveDetections >= 1 -> RiskLevel.LOW
                else -> RiskLevel.NONE
            }
        )
    }

    /**
     * Enhanced system setting check with better compatibility
     */
    private fun checkSystemSetting(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0+ - Mock location moved to app-specific permissions
                checkMockLocationAppsEnabled()
            } else {
                // Pre-Android 6.0
                val allowMockLocation = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ALLOW_MOCK_LOCATION
                )
                allowMockLocation == "1" || allowMockLocation == "true"
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if any app has mock location permission (Android 6.0+)
     */
    @SuppressLint("QueryPermissionsNeeded")
    private fun checkMockLocationAppsEnabled(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false

        return try {
            val packageManager = context.packageManager
            val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            packages.any { appInfo ->
                try {
                    // Check if app has mock location permission
                    packageManager.checkPermission(
                        "android.permission.ACCESS_MOCK_LOCATION",
                        appInfo.packageName
                    ) == PackageManager.PERMISSION_GRANTED
                } catch (e: Exception) {
                    false
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check for known mock location apps
     */
    @SuppressLint("QueryPermissionsNeeded")
    private fun checkInstalledMockApps(): Boolean {
        val knownMockApps = listOf(
            // Popular mock location apps
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
            // Developer tools that can mock location
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

    /**
     * Check for root access (many advanced spoofing methods require root)
     */
    private fun checkRootAccess(): Boolean {
        return try {
            // Check for common root binaries
            val rootPaths = arrayOf(
                "/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su",
                "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su",
                "/data/local/su", "/su/bin/su"
            )

            rootPaths.any { File(it).exists() } ||
                    checkRootManagementApps() ||
                    checkBuildTags()
        } catch (e: Exception) {
            false
        }
    }

    private fun checkRootManagementApps(): Boolean {
        val rootApps = listOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.koushikdutta.rommanager",
            "com.koushikdutta.rommanager.license",
            "com.dimonvideo.luckypatcher",
            "com.chelpus.lackypatch",
            "com.ramdroid.appquarantine",
            "com.ramdroid.appquarantinepro",
            "com.topjohnwu.magisk"
        )

        val packageManager = context.packageManager
        return rootApps.any { packageName ->
            try {
                packageManager.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private fun checkBuildTags(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    /**
     * Check for Xposed framework (commonly used for system-level spoofing)
     */
    private fun checkXposedFramework(): Boolean {
        return try {
            // Check for Xposed installer apps
            val xposedApps = listOf(
                "de.robv.android.xposed.installer",
                "com.solohsu.android.edxp.manager",
                "org.meowcat.edxposed.manager",
                "com.elderdrivers.riru.edxp",
                "me.weishu.exp"
            )

            val packageManager = context.packageManager
            val hasXposedApp = xposedApps.any { packageName ->
                try {
                    packageManager.getPackageInfo(packageName, 0)
                    true
                } catch (e: PackageManager.NameNotFoundException) {
                    false
                }
            }

            // Check for Xposed bridge class
            val hasXposedClass = try {
                Class.forName("de.robv.android.xposed.XposedBridge")
                true
            } catch (e: ClassNotFoundException) {
                false
            }

            // Check for Xposed files
            val xposedFiles = arrayOf(
                "/system/framework/XposedBridge.jar",
                "/system/xposed.prop",
                "/system/framework/riru-core.jar"
            )
            val hasXposedFiles = xposedFiles.any { File(it).exists() }

            hasXposedApp || hasXposedClass || hasXposedFiles
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check developer options status
     */
    private fun checkDeveloperOptions(): Boolean {
        return try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check debugging flags
     */
    private fun checkDebuggingFlags(): Boolean {
        return try {
            val appInfo = context.applicationInfo
            (appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0 ||
                    Settings.Secure.getInt(
                        context.contentResolver,
                        Settings.Global.ADB_ENABLED,
                        0
                    ) == 1
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Additional location-based checks
     */
    fun validateLocation(location: Location): LocationValidation {
        val issues = mutableListOf<String>()

        // Check if location is from mock provider
        if (location.isFromMockProvider) {
            issues.add("Location marked as mock")
        }

        // Check for suspicious accuracy
        if (location.hasAccuracy() && location.accuracy < 1.0f) {
            issues.add("Suspiciously high accuracy")
        }

        // Check for missing GPS extras
        if (location.provider == LocationManager.GPS_PROVIDER) {
            val bundle = location.extras
            if (bundle == null || !bundle.containsKey("satellites")) {
                issues.add("Missing GPS satellite data")
            }
        }

        // Check for suspicious altitude
        if (location.hasAltitude() && location.altitude == 0.0) {
            issues.add("Suspicious altitude value")
        }

        return LocationValidation(
            isValid = issues.isEmpty(),
            issues = issues,
            confidenceScore = calculateLocationConfidence(location, issues)
        )
    }

    private fun calculateConfidence(detectionMethods: Map<String, Boolean>): Int {
        val weights = mapOf(
            "system_setting" to 20,
            "installed_apps" to 25,
            "root_access" to 30,
            "xposed_framework" to 25,
            "developer_options" to 10,
            "debug_flags" to 5
        )

        val totalPossible = weights.values.sum()
        val detectedScore = detectionMethods.entries.sumOf { (method, detected) ->
            if (detected) weights[method] ?: 0 else 0
        }

        return ((detectedScore.toFloat() / totalPossible) * 100).toInt()
    }

    private fun calculateLocationConfidence(location: Location, issues: List<String>): Int {
        var confidence = 100
        issues.forEach { issue ->
            confidence -= when {
                issue.contains("mock") -> 40
                issue.contains("accuracy") -> 20
                issue.contains("satellite") -> 15
                issue.contains("altitude") -> 10
                else -> 5
            }
        }
        return maxOf(0, confidence)
    }
}

// Data classes for results
data class MockLocationResult(
    val isMockDetected: Boolean,
    val confidenceLevel: Int,
    val detectionMethods: Map<String, Boolean>,
    val riskLevel: RiskLevel
)

data class LocationValidation(
    val isValid: Boolean,
    val issues: List<String>,
    val confidenceScore: Int
)

enum class RiskLevel {
    NONE, LOW, MEDIUM, HIGH
}