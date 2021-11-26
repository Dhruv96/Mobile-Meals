package com.example.mobilemeals.models

class CartResponse(
    var message : String,
    var cart: CartItemsWithId
)

class CartItemsWithId(
    var _id: String,
    var items: List<CartItem>
)