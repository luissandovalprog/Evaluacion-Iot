package com.example.evaluacion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DeviceListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        // --- AÑADIR ESTA LÍNEA ---
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // -------------------------

        val recyclerView = findViewById<RecyclerView>(R.id.rv_device_list)

        // 1. Crear la lista simulada de dispositivos
        val deviceList = ArrayList<Device>()
        deviceList.add(Device("Detector de Lluvia"))

        // 2. Definir la acción de clic en un item
        val onDeviceClick: (Device) -> Unit = { device ->
            val intent = Intent(this, DeviceControlActivity::class.java)
            intent.putExtra("DEVICE_NAME", device.name)
            startActivity(intent)
        }

        // 3. Configurar el RecyclerView
        val adapter = DeviceListAdapter(deviceList, onDeviceClick)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
