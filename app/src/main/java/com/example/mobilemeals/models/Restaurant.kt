package com.example.mobilemeals.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "restaurants")
class Restaurant(
    @PrimaryKey()
    val _id : String,
    val name : String,
    val address : String,
    val city : String,
    val cuisine : String,
    val open_time : String,
    val close_time : String,
    val img_url : String
) : Serializable