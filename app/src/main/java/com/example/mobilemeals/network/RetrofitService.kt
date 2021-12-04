package com.example.mobilemeals.network

import com.example.mobilemeals.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*



interface RetrofitService {
    @POST("/user/signin")
    fun signIn(@Body user: User): Call<UserLoginResponse>

    @GET("/restaurant/all")
    fun getAllRestaurants(): Call<GetAllRestaurantsResponse>

    @POST("/user/signup")
    fun signUp(@Body userSignUp: UserSignUp): Call<UserLoginResponse>

    @POST("/restaurant/add")
    fun addRestaurant(@Body restaurant: Restaurant): Call<AddNewRestaurantResponse>

    @POST("/dish/add")
    fun addDish(@Body dish: Dish): Call<AddNewDishResponse>

    @GET("/dish/all/{restaurant_id}")
    fun getDishes(@Path("restaurant_id") restaurant_id: String): Call<GetDishesResponse>

    @GET("/dish/getSpecificDishes")
    fun getSpecificDishes(@Query("dishId") dishIds: List<String>): Call<GetSpecificDishesResponse>

    @POST("/cart/addItem")
    fun addItemToCart(@Body userCartItem: UserCartItem): Call<ResponseBody>

    @PUT("/cart/updateItem")
    fun updateCartItem(@Body userCartItem: UserCartItem): Call<ResponseBody>

    @GET("/cart/userCart/{userId}")
    fun getUserCart(@Path("userId") userId: String): Call<CartResponse>

    @DELETE("/cart/deleteItem/{userId}/{itemId}")
    fun deleteCartItem(@Path("userId") userId: String, @Path("itemId") itemId:String): Call<ResponseBody>

    @DELETE("/cart/clearCart/{userId}")
    fun clearCart(@Path("userId") userId: String): Call<ResponseBody>

    @POST("/orders/addNewOrder")
    fun addNewOrder(@Body order: BodyForPostingOrder) : Call<ResponseBody>

    @GET("/orders/{userId}")
    fun getUserOrders(@Path("userId") userId:String) : Call<UserOrdersResponse>

    @GET("/restaurant/{id}")
    fun getSpecificRestaurant(@Path("id") restaurantId: String): Call<GetSpecificRestaurantResponse>

    @PUT("/user/updateUser")
    fun updateUserProfile(@Body user: UpdateUserProfileRequest): Call<ResponseBody>

    @DELETE("/restaurant/delete/{restaurant_id}")
    fun deleteRestaurant(@Path("restaurant_id") restaurantId: String) : Call<ResponseBody>

    @DELETE("/dish/delete/{dishId}")
    fun deleteDish(@Path("dishId") dishId: String): Call<ResponseBody>

    @POST("/restaurants/ratings/addNewRating")
    fun addNewRating(@Body userRating: UserRating): Call<ResponseBody>

    @GET("/restaurants/ratings/{restaurantId}")
    fun getRestaurantRating(@Path("restaurantId") restaurantId: String): Call<GetRestaurantRatingResponse>

    @PUT("/dish/edit/{dishId}")
    fun editDish(@Path("dishId") dishId: String, @Body dish: Dish): Call<ResponseBody>
}