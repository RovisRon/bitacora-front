package com.example.logros

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.logros.dataClasses.Achievement
import com.example.logros.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AchievementsActivity : AppCompatActivity() {

    private lateinit var buttons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        buttons = listOf(
            findViewById(R.id.achievementButton1),
            findViewById(R.id.achievementButton2),
            findViewById(R.id.achievementButton3),
            findViewById(R.id.achievementButton4),
            findViewById(R.id.achievementButton5),
            findViewById(R.id.achievementButton6),
            findViewById(R.id.achievementButton7),
            findViewById(R.id.achievementButton8),
            findViewById(R.id.achievementButton9),
            findViewById(R.id.achievementButton10),
            findViewById(R.id.achievementButton11),
            findViewById(R.id.achievementButton12),
            findViewById(R.id.achievementButton13),
            findViewById(R.id.achievementButton14),
            findViewById(R.id.achievementButton15),
            findViewById(R.id.achievementButton16),
            findViewById(R.id.achievementButton17)
        )

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

        val categoryId = intent.getStringExtra("CATEGORY_ID")?.toInt() ?: return
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.15:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val callAchievements = apiService.getAchievements()
        callAchievements.enqueue(object : Callback<List<Achievement>> {
            override fun onResponse(call: Call<List<Achievement>>, response: Response<List<Achievement>>) {
                if (response.isSuccessful && response.body() != null) {
                    val achievements = response.body()!!.filter { it.categoryId == categoryId }
                    updateButtonsWithAchievements(achievements)
                } else {
                    Toast.makeText(this@AchievementsActivity, "Error en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Achievement>>, t: Throwable) {
                Toast.makeText(this@AchievementsActivity, "Error en la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateButtonsWithAchievements(achievements: List<Achievement>) {
        if (achievements.size >= 17) {
            buttons.forEachIndexed { index, button ->
                button.text = achievements[index].title
                button.setOnClickListener {
                    navigateToAchievementDetail(achievements[index])
                }
            }
        } else {
            Toast.makeText(this, "No hay suficientes logros", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToAchievementDetail(achievement: Achievement) {
        val intent = Intent(this, AchievementDetailActivity::class.java).apply {
            putExtra("ACHIEVEMENT_ID", achievement.id)
            putExtra("TITLE", achievement.title)
            putExtra("DESCRIPTION", achievement.description)
        }
        startActivity(intent)
    }
}