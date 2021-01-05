package com.snaggly.ksw_toolkit.core.service

import android.app.AlertDialog
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import com.snaggly.ksw_toolkit.BuildConfig
import com.snaggly.ksw_toolkit.util.adb.AdbManager
import projekt.auto.mcu.adb.lib.AdbStream
import projekt.auto.mcu.ksw.serial.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.McuEvent
import projekt.auto.mcu.ksw.serial.SerialReader
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class McuService : Service() {
    inner class McuServiceBinder : Binder() {
        fun getService(): McuService = this@McuService
    }

    private val binder = McuServiceBinder()
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    interface ShellObserver {
        fun update()
    }

    interface McuEventObserver {
        fun update(eventType: McuEvent?, cmdType: Int, data: ByteArray)
    }

    private val mcuEventListeners = ArrayList<McuEventObserver>()
    private val eventLogic = McuEventLogicImpl()
    private val adbManager = AdbManager()
    private val adbShellListeners = ArrayList<ShellObserver>()
    val adbLines = ArrayList<String>()

    private var mcuReader: McuCommunicator.Reader? = null

    override fun onCreate() {
        super.onCreate()
        try {
            adbManager.connect(applicationContext, "shell:", object : AdbManager.OnAdbShellDataReceived {
                override fun onDataReceived(text: String) {
                    adbLines.add(text)
                    Log.d("Snaggly", "McuService adbShell OnDataReceived - Text: $text - ListenersSize: ${adbShellListeners.size}")
                    for (listener in adbShellListeners)
                        listener.update()
                }
            })
        } catch (e: Exception) {
            Log.d("KSW-ToolKit-McuService", e.localizedMessage!!)
            var alert = AlertDialog.Builder(this).setTitle("KSW-ToolKit-McuService").setMessage("Could not connect to Adb!\n\n${e.localizedMessage}").create()
            alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            alert.show()
            stopSelf()
            return
        }

        checkPermission()
        startMcuReader()
        Log.d("Snaggly", "Started McuService")
    }

    override fun onDestroy() {
        super.onDestroy()
        mcuReader?.stopReading()
        adbManager.disconnect()
        Log.d("Snaggly", "Stopped McuService")
    }


    private fun checkPermission() {
        if (applicationContext.checkSelfPermission("android.permission.READ_LOGS") != PackageManager.PERMISSION_GRANTED) {
            sendAdbCommand("pm grant ${BuildConfig.APPLICATION_ID} android.permission.READ_LOGS")
            var alert = AlertDialog.Builder(this).setTitle("KSW-ToolKit-McuService").setMessage("Granted READ_LOGS permission.\nPlease restart the app for effects to take in place").create()
            alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            alert.show()
        }
    }

    private fun startMcuReader() {
        mcuReader = LogcatReader()
        LogcatReader().startReading { cmdType, data ->
            for (mcuEventListener in mcuEventListeners)
                mcuEventListener.update(eventLogic.getMcuEvent(cmdType, data), cmdType, data)
        }
        Log.d("Snaggly", "Started McuServiceReader")
    }

    fun sendAdbCommand(command: String) {
        adbManager.sendCommand(command)
    }

    fun registerShellListener(listener: ShellObserver) {
        adbShellListeners.add(listener)
    }

    fun unregisterShellListener(listener: ShellObserver) {
        adbShellListeners.remove(listener)
    }

    fun registerMcuEventListener(listener: McuEventObserver) {
        mcuEventListeners.add(listener)
    }

    fun unregisterMcuEventListener(listener: McuEventObserver) {
        mcuEventListeners.remove(listener)
    }
}
