package com.example.mobilemeals.models

class CartItem(
    var itemId: String,
    var quantity: Int
)

class UserCartItem(
    var _id: String,
    var cart_item: CartItem
)