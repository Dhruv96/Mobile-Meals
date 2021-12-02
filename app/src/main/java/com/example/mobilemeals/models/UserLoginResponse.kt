package com.example.mobilemeals.models

import com.google.gson.annotations.SerializedName

class UserLoginResponse (
    @SerializedName("_id")val _id: String,
    @SerializedName("email")val email: String,
    @SerializedName("name")val name: String,
    @SerializedName("address")val address: String,
    @SerializedName("city")val city: String,
)


class UpdateUserProfileRequest(
    @SerializedName("_id")val _id: String,
    @SerializedName("email")val email: String,
    @SerializedName("name")val name: String,
    @SerializedName("address")val address: String,
    @SerializedName("city")val city: String,
    var password: String
)