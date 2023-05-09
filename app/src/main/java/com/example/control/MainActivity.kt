package com.example.control

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val DEVICE_ADDRESS = "34:43:01:05:90:22" // Dirección MAC del dispositivo Bluetooth
    private val PORT_UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Identificador UUID para el servicio SPP (Serial Port Profile)
    val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(DEVICE_ADDRESS)
    private var socket: BluetoothSocket? = null
    private lateinit var btnConnect: Button

    private val handler = Handler()


    private val butLeft = object : Runnable {
        override fun run() {
            sendCommand("A")
            handler.postDelayed(this, 10)
            println("Izquierda")
        }
    }

    private val butUp = object : Runnable {
        override fun run() {
            sendCommand("W")
            handler.postDelayed(this, 10)
            println("Adelante")
        }
    }

    private val butRight = object : Runnable {
        override fun run() {
            sendCommand("D")
            handler.postDelayed(this, 10)
            println("Derecha")
        }
    }

    private val butDown = object : Runnable {
        override fun run() {
            sendCommand("S")
            handler.postDelayed(this, 10)
            println("Atras")
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_main)

        btnConnect = findViewById(R.id.bluetooth)
        btnConnect.setOnClickListener { v: View -> connect() }

        val up = findViewById<Button>(R.id.avanza)
        up.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.postDelayed(butUp, 10)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(butUp)
                    sendCommand("P")
                    true
                }
                else -> false
            }
        }
        val left = findViewById<Button>(R.id.izquierda)
        left.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.postDelayed(butLeft, 10)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(butLeft)
                    sendCommand("P")
                    true
                }
                else -> false
            }
        }
        val down = findViewById<Button>(R.id.atras)
        down.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.postDelayed(butDown, 10)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(butDown)
                    sendCommand("P")
                    true
                }
                else -> false
            }
        }
        val right = findViewById<Button>(R.id.derecha)
        right.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.postDelayed(butRight, 10)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(butRight)
                    sendCommand("P")
                    true
                }
                else -> false
            }
        }

        val rgb = findViewById<Button>(R.id.rgb)
        rgb.setOnClickListener { v: View -> sendCommand("L") }

        val atk1 = findViewById<Button>(R.id.ataque1)
        atk1.setOnClickListener { v: View -> sendCommand("1") }

        val atk2 = findViewById<Button>(R.id.ataque2)
        atk2.setOnClickListener { v: View -> sendCommand("2") }


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        actionBar?.hide()
    }

    private fun connect() {
        try {
            /*if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(applicationContext, "No permission", Toast.LENGTH_SHORT).show()
                return
            }*/
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
            Toast.makeText(applicationContext, "Not connected", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            socket?.outputStream?.write(command.toByteArray())
            // Toast.makeText(applicationContext, "Command sent: $command", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e(TAG, "Error sending command: ${e.message}")
            Toast.makeText(applicationContext, "Error sending command: ${e.message}", Toast.LENGTH_SHORT).show()
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

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(butUp)
        handler.removeCallbacks(butLeft)
        handler.removeCallbacks(butRight)
        handler.removeCallbacks(butDown)
    }
}