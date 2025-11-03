package com.example.evaluacion
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class DeviceControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control)

        // --- AÑADIR ESTAS LÍNEAS PARA LA TOOLBAR ---
        val toolbar = findViewById<Toolbar>(R.id.toolbar_control)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Opcional: añade flecha de "atrás"
        // ------------------------------------------

        // Encontrar las vistas
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        val tvDeviceTitle = findViewById<TextView>(R.id.tv_device_title) // Este sigue aquí
        val btnOpenWindow = findViewById<Button>(R.id.btn_open_window)
        val btnCloseWindow = findViewById<Button>(R.id.btn_close_window)

        // (Opcional) Obtener el nombre del dispositivo del Intent
        val deviceName = intent.getStringExtra("DEVICE_NAME")

        // --- MOVER EL TÍTULO A LA TOOLBAR Y AL TEXTVIEW ---
        supportActionBar?.title = deviceName ?: "Control" // Título en la Toolbar
        tvDeviceTitle.text = deviceName ?: "Control del Dispositivo" // Título en el layout

        // Estado inicial
        tvStatus.text = "Estado: Sin lluvia. Ventana abierta."

        // Lógica simulada para los botones
        btnOpenWindow.setOnClickListener {
            tvStatus.text = "Comando manual: Ventana abierta"
        }

        btnCloseWindow.setOnClickListener {
            tvStatus.text = "Comando manual: Ventana cerrada"
        }
    }

    // --- AÑADIR ESTA FUNCIÓN (para la flecha de "atrás") ---
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
    // ----------------------------------------------------
}
