package com.example.mobilemeals.network

import com.example.mobilemeals.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface RetrofitService {
    @POST("/user/signin")
    fun signIn(@Body user: User): Call<UserLoginResponse>

    @GET("/restaurant/all")
    fun getAllRestaurants(): Call<GetAllRestaurantsResponse>

    @POST("/user/signup")
    fun signUp(@Body userSignUp: UserSignUp): Call<UserSignUpResponse>
}