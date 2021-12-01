package com.example.mobilemeals.models

import java.io.Serializable

class MealOrder(
    var final_price: Double,
    var item_string: String,
    var restaurant_id: String
)

class BodyForPostingOrder(
    var _id: String,
    var order: MealOrder
): Serializable