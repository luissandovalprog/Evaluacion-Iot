package com.example.evaluacion // ¡ASEGÚRATE DE QUE ESTA LÍNEA SEA CORRECTA!

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esta es la línea que carga el layout correcto
        setContentView(R.layout.activity_main)

        // Encontrar las vistas por su ID
        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        // Configurar el listener para el botón de login
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            // Lógica de autenticación simulada (hardcoded)
            if (username == "admin" && password == "1234") {
                // Navegar a la siguiente pantalla si es correcto
                val intent = Intent(this, DeviceListActivity::class.java)
                startActivity(intent)
                finish() // Opcional: cierra la activity de login
            } else {
                // Mostrar error si es incorrecto
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
