package com.example.mobilemeals.models

class UserRating(
    val _id: String,
    val userId: String,
    val rating: Float,
    val feedback: String,
    val restaurantId: String
)