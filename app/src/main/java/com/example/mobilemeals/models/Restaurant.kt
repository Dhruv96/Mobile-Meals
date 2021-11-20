package com.example.mobilemeals.models

import java.io.Serializable

class Restaurant(
    val _id : String,
    val name : String,
    val address : String,
    val city : String,
    val cuisine : String,
    val open_time : String,
    val close_time : String,
    val img_url : String
) : Serializable