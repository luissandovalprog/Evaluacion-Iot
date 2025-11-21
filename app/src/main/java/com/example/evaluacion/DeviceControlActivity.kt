package com.example.evaluacion

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import java.util.UUID

class DeviceControlActivity : AppCompatActivity() {

    // UUIDs COPIADOS DE TU CÓDIGO ARDUINO
    private val SERVICE_UUID = UUID.fromString("12345678-90ab-cdef-1234-567890abcdef")
    private val CONTROL_CHAR_UUID = UUID.fromString("abcdef02-1234-5678-90ab-cdef12345678")

    // Variable para mantener la conexión BLE
    private var bluetoothGatt: BluetoothGatt? = null
    private var controlCharacteristic: BluetoothGattCharacteristic? = null

    private lateinit var tvStatus: TextView
    private var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control)

        setupUI()

        val deviceAddress = intent.getStringExtra("DEVICE_ADDRESS")
        if (deviceAddress != null) {
            connectToBLEDevice(deviceAddress)
        }
    }

    private fun setupUI() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_control)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra("DEVICE_NAME") ?: "Control BLE"

        tvStatus = findViewById(R.id.tv_status)

        findViewById<Button>(R.id.btn_open_window).setOnClickListener {
            // En tu Arduino: 'b' es abrir ventana (write(0))
            sendBLECommand("b")
        }

        findViewById<Button>(R.id.btn_close_window).setOnClickListener {
            // En tu Arduino: 'a' es cerrar ventana (write(90))
            sendBLECommand("a")
        }
    }

    private fun connectToBLEDevice(address: String) {
        tvStatus.text = "Estado: Conectando a BLE..."

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter
        val device = adapter.getRemoteDevice(address)

        // Conexión GATT para BLE
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    // Callback que maneja los eventos de Bluetooth BLE
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread { tvStatus.text = "Estado: Conectado. Buscando servicios..." }
                isConnected = true

                // Importante: Permiso necesario para descubrir servicios
                if (ActivityCompat.checkSelfPermission(this@DeviceControlActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    gatt?.discoverServices()
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnected = false
                runOnUiThread { tvStatus.text = "Estado: Desconectado" }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt?.getService(SERVICE_UUID)
                if (service != null) {
                    controlCharacteristic = service.getCharacteristic(CONTROL_CHAR_UUID)
                    runOnUiThread {
                        tvStatus.text = "Estado: Listo para controlar"
                        Toast.makeText(this@DeviceControlActivity, "Servicio Arduino encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread { tvStatus.text = "Error: No se encontró el Servicio UUID" }
                }
            }
        }
    }

    private fun sendBLECommand(command: String) {
        if (bluetoothGatt == null || controlCharacteristic == null) {
            Toast.makeText(this, "Bluetooth no inicializado", Toast.LENGTH_SHORT).show()
            return
        }

        // Permiso check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // Escribir el valor en la característica
        controlCharacteristic?.setValue(command)
        controlCharacteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT

        // Enviar la orden
        val success = bluetoothGatt?.writeCharacteristic(controlCharacteristic) ?: false

        if (success) {
            val accion = if(command == "a") "Cerrar" else "Abrir"
            tvStatus.text = "Comando enviado: $accion"
        } else {
            tvStatus.text = "Error al enviar comando"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            bluetoothGatt?.close()
        }
        bluetoothGatt = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}