package com.snaggly.ksw_toolkit.util.adb

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.snaggly.ksw_toolkit.util.commander.AppStarter
import kotlin.math.min

object DensityUtil {
    private var originalDpi = -1
    private var tabletModeTurnedOn = false

    fun turnOnTabletMode(context: Context) {
        if (tabletModeTurnedOn)
            return
        val displayMetrics = context.resources.displayMetrics
        if (originalDpi < 0) {
            originalDpi = displayMetrics.densityDpi
        }
        val tabletDpi = Integer.max(
            (min(
                displayMetrics.widthPixels,
                displayMetrics.heightPixels
            ) * 160) / Integer.max(600, 320), 120
        )
        tabletModeTurnedOn = try {
            AdbManager.sendCommand("wm density $tabletDpi", context)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun turnOffTabletMode(context: Context) {
        if (!tabletModeTurnedOn)
            return
        val dpi = if (originalDpi < 0) {
            if (context.resources.displayMetrics.heightPixels > 480)
                240
            else
                160
        } else {
            originalDpi
        }
        tabletModeTurnedOn = try {
            AdbManager.sendCommand("wm density $dpi", context)
            false
        } catch (e: Exception) {
            true
        }
    }
}