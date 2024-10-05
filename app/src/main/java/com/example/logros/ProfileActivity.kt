package com.example.logros

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.logros.dataClasses.BioUpdateRequest
import com.example.logros.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var mailTextView: TextView
    private lateinit var biographyTextMultiLine: EditText
    private lateinit var deleteAccountButton: Button
    private lateinit var saveButton: Button
    private lateinit var username: String
    private lateinit var mail: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPreferences.getString("userId", null)

        username = sharedPreferences.getString("username", null).toString()
        mail = sharedPreferences.getString("mail", null).toString()

        usernameTextView = findViewById(R.id.usernameTextView)
        mailTextView = findViewById(R.id.mailTextView)
        biographyTextMultiLine = findViewById(R.id.biographyTextMultiLine)
        deleteAccountButton = findViewById(R.id.deleteAccountButton)
        saveButton = findViewById(R.id.saveButton)

        usernameTextView.text = username
        mailTextView.text = mail

        // FOOTER
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val profileButton: Button = findViewById(R.id.profileButton)
        val supportButton: Button = findViewById(R.id.supportButton)
        val homeButton: Button = findViewById(R.id.homeButton)
        supportButton.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            if (isLoggedIn) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // FOOTER

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.15:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        saveButton.setOnClickListener {
            val newBiography = biographyTextMultiLine.text.toString()
            updateUserBiography(newBiography)
        }

        deleteAccountButton.setOnClickListener {
            showDeleteAccountConfirmationDialog()
        }
    }

    private fun showDeleteAccountConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmación")
            setMessage("¿Estás seguro de que deseas eliminar tu cuenta?")
            setPositiveButton("Sí") { _, _ -> deleteUserAccount() }
            setNegativeButton("No", null)
        }.create().show()
    }

    private fun deleteUserAccount() {
        val userId = sharedPreferences.getString("userId", null) ?: "1"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.deleteUser(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        sharedPreferences.edit().clear().apply()
                        Toast.makeText(this@ProfileActivity, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserBiography(newBiography: String) {
        val userId = sharedPreferences.getString("user_Id", null) ?: ""
        println("Actualizando biografía del usuario id: $userId con la biografía: $newBiography")

        val bioUpdateRequest = BioUpdateRequest(newBiography)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateUserBio(userId, bioUpdateRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfileActivity, "Biografía actualizada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Error al actualizar la biografía", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}