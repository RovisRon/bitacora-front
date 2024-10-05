package com.example.logros

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.logros.dataClasses.Category
import com.example.logros.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)

        // FOOTER
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
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

        val apiService = retrofit.create(ApiService::class.java)

        val callCategories = apiService.getCategories()
        callCategories.enqueue(object : Callback<List<Category>> {
            override fun onResponse(
                call: Call<List<Category>>,
                response: Response<List<Category>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val categories = response.body()!!

                    if (categories.size > 2) {
                        button1.text = categories[0].name
                        button2.text = categories[1].name
                        button3.text = categories[2].name
                        button4.text = categories[3].name
                        button5.text = categories[4].name
                        button6.text = categories[5].name

                        button1.setOnClickListener {
                            navigateToAchievements(categories[0].id)
                        }

                        button2.setOnClickListener {
                            navigateToAchievements(categories[1].id)
                        }

                        button3.setOnClickListener {
                            navigateToAchievements(categories[2].id)
                        }

                        button4.setOnClickListener {
                            navigateToAchievements(categories[3].id)
                        }

                        button5.setOnClickListener {
                            navigateToAchievements(categories[4].id)
                        }

                        button6.setOnClickListener {
                            navigateToAchievements(categories[5].id)
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "No hay suficientes categorías",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error en la respuesta", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Error en la solicitud: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun navigateToAchievements(categoryId: String) {
        val intent = Intent(this, AchievementsActivity::class.java)
        intent.putExtra("CATEGORY_ID", categoryId)
        startActivity(intent)
    }
}