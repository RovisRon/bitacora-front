package com.example.logros.dataClasses

import com.google.gson.annotations.SerializedName

data class Achievement(
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("id") val id: String
)