package com.example.logros

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.logros.dataClasses.User
import com.example.logros.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("UNUSED_EXPRESSION")
class RegisterActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var usernameEditText: EditText
    private lateinit var birthdayEditText: EditText
    private lateinit var mailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        usernameEditText = findViewById(R.id.usernameEditText)
        birthdayEditText = findViewById(R.id.birthdayEditText)
        mailEditText = findViewById(R.id.mailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.15:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val birthday = birthdayEditText.text.toString()
            val mail = mailEditText.text.toString()
            val password = passwordEditText.text.toString()
            ""

            if (username.isNotEmpty() && birthday.isNotEmpty() && mail.isNotEmpty() && password.isNotEmpty()) {
                registerUser(username, birthday, mail, password)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(username: String, birthday: String, mail: String, password: String) {
        val user = User(id = "", username = username, password = password, birthday = birthday, mail = mail, biography = "")
        val call = apiService.createUser(user)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", true)
                        apply()
                    }

                    Toast.makeText(this@RegisterActivity, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error en la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
