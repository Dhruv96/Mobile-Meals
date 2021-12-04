package com.example.mobilemeals.models

import java.io.Serializable

class Dish(
    val _id: String,
    val name: String,
    val category:String,
    val price :String,
    val restaurant_id: String,
    val img_url : String
) : Serializable