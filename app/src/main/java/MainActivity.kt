package com.example.mobilequizapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilequizapp.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // IP 10.0.2.2 to specjalny adres dla emulatora Androida, który wskazuje na 'localhost' Twojego komputera (XAMPP)
    private val apiUrl = "http://192.168.100.6/api/login.php"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }

        val btnGoToRegister = findViewById<Button>(R.id.btnGoToRegister)

        btnGoToRegister.setOnClickListener {
            // Otwieramy ekran rejestracji
            val intent = android.content.Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(user: String, pass: String) {
        // Tworzymy ciało zapytania (symulacja formularza POST)
        val formBody = FormBody.Builder()
            .add("username", user)
            .add("password", pass)
            .build()

        val request = Request.Builder()
            .url(apiUrl)
            .post(formBody)
            .build()

        // Wykonujemy zapytanie w tle (nie blokujemy interfejsu)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Błąd połączenia: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (responseData != null) {
                    try {
                        // Odbieramy JSON z PHP i sprawdzamy status
                        val json = JSONObject(responseData)
                        val status = json.getString("status")
                        val message = json.getString("message")

                        runOnUiThread {
                            if (status == "success") {
                                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()

                                // Przechodzimy do nowego ekranu WelcomeActivity
                                val intent = android.content.Intent(this@MainActivity, WelcomeActivity::class.java)
                                startActivity(intent)

                                // Zamykamy ekran logowania (aby po kliknięciu wstecz nie wrócić do logowania)
                                finish()

                            } else {
                                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            // Skracamy tekst do 100 znaków, żeby zmieścił się na ekranie telefonu
                            val podglad = responseData?.take(100) ?: "Pusto"
                            Toast.makeText(this@MainActivity, "Odpowiedź serwera: $podglad", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }
}