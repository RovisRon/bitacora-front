package com.example.logros

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.logros.dataClasses.UserAchievement
import com.example.logros.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AchievementDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var completeButton: Button
    private lateinit var apiService: ApiService
    private var achievementId: String? = null
    private var userId: String? = null
    private var id: String? = null
    private var isCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievement_detail)

        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        completeButton = findViewById(R.id.completeButton)

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

        userId = sharedPreferences.getString("user_id", null)

        if (userId == null) {
            Toast.makeText(this, "Error al cargar el logro", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        achievementId = intent.getStringExtra("ACHIEVEMENT_ID")

        completeButton.setOnClickListener {
            toggleCompletionStatus()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.15:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        loadAchievementDetails()
        checkUserAchievementStatus()
    }

    @SuppressLint("SetTextI18n")
    private fun toggleCompletionStatus() {
        isCompleted = !isCompleted
        if (isCompleted) {
            completeButton.text = "Completado"
            completeButton.setBackgroundColor(getColor(android.R.color.holo_green_light))
            completeAchievement()
        } else {
            completeButton.text = "No completado"
            completeButton.setBackgroundColor(getColor(android.R.color.holo_red_light))
            unCompleteAchievement()
        }
    }

    private fun loadAchievementDetails() {
        val title = intent.getStringExtra("TITLE")
        val description = intent.getStringExtra("DESCRIPTION")

        titleTextView.text = title
        descriptionTextView.text = description
    }

    private fun checkUserAchievementStatus() {
        val callUserAchievements = apiService.getUserAchievements()
        callUserAchievements.enqueue(object : Callback<List<UserAchievement>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<List<UserAchievement>>, response: Response<List<UserAchievement>>) {
                if (response.isSuccessful && response.body() != null) {
                    val userAchievements = response.body()!!
                    val userAchievement = userAchievements.find {
                        it.userid == userId && it.achievementid == achievementId
                    }

                    if (userAchievement != null) {
                        id = userAchievement.id
                        isCompleted = true
                        completeButton.text = "Completado"
                        completeButton.setBackgroundColor(getColor(android.R.color.holo_green_light))
                    } else {
                        isCompleted = false
                        completeButton.text = "No Completado"
                        completeButton.setBackgroundColor(getColor(android.R.color.holo_red_light))
                    }
                }
            }

            override fun onFailure(call: Call<List<UserAchievement>>, t: Throwable) {
                Toast.makeText(this@AchievementDetailActivity, "Error en la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun unCompleteAchievement() {
    }
    private fun completeAchievement() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val userAchievement = UserAchievement(
            id = "",
            userid = userId!!,
            achievementid = achievementId!!,
            completationdate = currentDate
        )

        val call = apiService.updateUserAchievement(userAchievement.id, userAchievement)
        call.enqueue(object : Callback<UserAchievement> {
            override fun onResponse(call: Call<UserAchievement>, response: Response<UserAchievement>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AchievementDetailActivity, "Logro completado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AchievementDetailActivity, "Error al completar el logro", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserAchievement>, t: Throwable) {
                Toast.makeText(this@AchievementDetailActivity, "Error en la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}