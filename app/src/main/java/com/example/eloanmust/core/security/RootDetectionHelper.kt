package com.example.eloanmust.core.security

import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Helper class to detect if the device is rooted.
 * Uses multiple detection methods for comprehensive coverage.
 */
object RootDetectionHelper {

    private val knownRootAppsPackages = arrayOf(
        "com.noshufou.android.su",
        "com.noshufou.android.su.elite",
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.thirdparty.superuser",
        "com.yellowes.su",
        "com.topjohnwu.magisk",
        "com.kingroot.kinguser",
        "com.kingo.root",
        "com.smedialink.oneclickroot",
        "com.zhiqupk.root.global",
        "com.alephzain.framaroot"
    )

    private val knownDangerousAppsPackages = arrayOf(
        "com.koushikdutta.rommanager",
        "com.koushikdutta.rommanager.license",
        "com.dimonvideo.luckypatcher",
        "com.chelpus.lackypatch",
        "com.ramdroid.appquarantine",
        "com.ramdroid.appquarantinepro",
        "com.android.vending.billing.InAppBillingService.COIN",
        "com.android.vending.billing.InAppBillingService.LUCK",
        "com.chelpus.luckypatcher",
        "com.blackmartalpha",
        "org.blackmart.market"
    )

    private val suPaths = arrayOf(
        "/data/local/",
        "/data/local/bin/",
        "/data/local/xbin/",
        "/sbin/",
        "/su/bin/",
        "/system/bin/",
        "/system/bin/.ext/",
        "/system/bin/failsafe/",
        "/system/sd/xbin/",
        "/system/usr/we-need-root/",
        "/system/xbin/",
        "/cache/",
        "/data/",
        "/dev/"
    )

    /**
     * Perform comprehensive root detection.
     * Returns true if device appears to be rooted.
     */
    fun isDeviceRooted(): Boolean {
        return checkRootMethod1() || 
               checkRootMethod2() || 
               checkRootMethod3() ||
               checkRootMethod4()
    }

    /**
     * Get detailed root detection result for logging/debugging
     */
    fun getRootDetectionDetails(): RootDetectionResult {
        val buildTagsRooted = checkRootMethod1()
        val suBinaryExists = checkRootMethod2()
        val suCommandWorks = checkRootMethod3()
        val rootAppsInstalled = checkRootMethod4()
        
        return RootDetectionResult(
            isRooted = buildTagsRooted || suBinaryExists || suCommandWorks || rootAppsInstalled,
            buildTagsRooted = buildTagsRooted,
            suBinaryExists = suBinaryExists,
            suCommandWorks = suCommandWorks,
            rootAppsInstalled = rootAppsInstalled,
            detectedApps = getInstalledRootApps()
        )
    }

    /**
     * Check 1: Build Tags
     * Check if build tags contain "test-keys" which indicates a custom ROM
     */
    private fun checkRootMethod1(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    /**
     * Check 2: SU Binary Existence
     * Check common paths for su binary
     */
    private fun checkRootMethod2(): Boolean {
        for (path in suPaths) {
            if (File(path + "su").exists()) {
                return true
            }
        }
        return false
    }

    /**
     * Check 3: SU Command Execution
     * Try to execute su command (will fail on non-rooted devices)
     */
    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val line = reader.readLine()
            line != null
        } catch (e: Exception) {
            false
        } finally {
            process?.destroy()
        }
    }

    /**
     * Check 4: Root Management Apps
     * Check if known root management apps are installed
     */
    private fun checkRootMethod4(): Boolean {
        val allPackages = knownRootAppsPackages + knownDangerousAppsPackages
        for (packageName in allPackages) {
            try {
                val file = File("/data/data/$packageName")
                if (file.exists()) {
                    return true
                }
            } catch (e: Exception) {
                // Continue checking other packages
            }
        }
        return false
    }

    /**
     * Get list of detected root apps (for logging)
     */
    private fun getInstalledRootApps(): List<String> {
        val installedApps = mutableListOf<String>()
        val allPackages = knownRootAppsPackages + knownDangerousAppsPackages
        for (packageName in allPackages) {
            try {
                val file = File("/data/data/$packageName")
                if (file.exists()) {
                    installedApps.add(packageName)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        return installedApps
    }
}

/**
 * Data class containing detailed root detection results
 */
data class RootDetectionResult(
    val isRooted: Boolean,
    val buildTagsRooted: Boolean,
    val suBinaryExists: Boolean,
    val suCommandWorks: Boolean,
    val rootAppsInstalled: Boolean,
    val detectedApps: List<String>
)
