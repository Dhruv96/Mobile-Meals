package com.example.mobilemeals.network

import com.example.mobilemeals.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface RetrofitService {
    @POST("/user/signin")
    fun signIn(@Body user: User): Call<UserLoginResponse>

    @GET("/restaurant/all")
    fun getAllRestaurants(): Call<GetAllRestaurantsResponse>

    @POST("/user/signup")
    fun signUp(@Body userSignUp: UserSignUp): Call<UserSignUpResponse>

    @POST("/restaurant/add")
    fun addRestaurant(@Body restaurant: Restaurant): Call<AddNewRestaurantResponse>

    @POST("/dish/add")
    fun addDish(@Body dish: Dish): Call<AddNewDishResponse>

    @GET("/dish/all/{restaurant_id}")
    fun getDishes(@Path("restaurant_id") restaurant_id: String): Call<GetDishesResponse>
}