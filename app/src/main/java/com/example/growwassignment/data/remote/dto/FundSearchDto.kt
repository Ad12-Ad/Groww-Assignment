package com.example.growwassignment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FundSearchDto(
    @SerializedName("schemeCode") val schemeCode: Int,
    @SerializedName("schemeName") val schemeName: String
)