package com.example.logros.dataClasses

import com.google.gson.annotations.SerializedName

data class BioUpdateRequest(
    @SerializedName("biography") val biography: String
)