package com.example.control

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val DEVICE_ADDRESS = "34:43:01:05:90:22" // Dirección MAC del dispositivo Bluetooth
    private val PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Identificador UUID para el servicio SPP (Serial Port Profile)
    val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(DEVICE_ADDRESS)
    private var socket: BluetoothSocket? = null
    private lateinit var btnConnect: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_main)

        btnConnect = findViewById(R.id.bluetooth)
        btnConnect.setOnClickListener { v: View -> connect() }

        val btn1 = findViewById<Button>(R.id.avanza)
        btn1.setOnClickListener { v: View -> sendCommand("W") }

        val btn2 = findViewById<Button>(R.id.izquierda)
        btn2.setOnClickListener { v: View -> sendCommand("A") }

        val btn3 = findViewById<Button>(R.id.atras)
        btn3.setOnClickListener { v: View -> sendCommand("S") }

        val btn4 = findViewById<Button>(R.id.derecha)
        btn4.setOnClickListener { v: View -> sendCommand("D") }

        val btn5 = findViewById<Button>(R.id.rgb)
        btn5.setOnClickListener { v: View -> sendCommand("L") }

        val btn6 = findViewById<Button>(R.id.ataque1)
        btn6.setOnClickListener { v: View -> sendCommand("1") }

        val btn7 = findViewById<Button>(R.id.ataque2)
        btn7.setOnClickListener { v: View -> sendCommand("2") }

        val btn8 = findViewById<Button>(R.id.stop)
        btn8.setOnClickListener { v: View -> sendCommand("P") }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        actionBar?.hide()
    }

    private fun connect() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(applicationContext, "No permission", Toast.LENGTH_LONG).show()
                return
            }
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID)

            socket?.connect()
            Log.d(TAG, "Connected")
            Toast.makeText(applicationContext, "Connected", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e(TAG, "Error connecting: ${e.message}")
            Toast.makeText(applicationContext, "Error connecting: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendCommand(command: String) {
        if (socket == null) {
            Toast.makeText(applicationContext, "Not connected", Toast.LENGTH_LONG).show()
            return
        }

        try {
            socket?.outputStream?.write(command.toByteArray())
            Toast.makeText(applicationContext, "Command sent: $command", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e(TAG, "Error sending command: ${e.message}")
            Toast.makeText(applicationContext, "Error sending command: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            socket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing socket: ${e.message}")
        }
    }
}