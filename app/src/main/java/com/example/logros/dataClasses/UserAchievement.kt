package com.example.logros.dataClasses

import com.google.gson.annotations.SerializedName

data class UserAchievement(
    @SerializedName("id") val id: String,
    @SerializedName("achievementid") val achievementid: String,
    @SerializedName("userid") val userid: String,
    @SerializedName("completationdate") val completationdate: String
)