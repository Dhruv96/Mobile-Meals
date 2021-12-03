package com.example.mobilemeals.Database

import androidx.room.*
import com.example.mobilemeals.models.Restaurant

@Dao
interface RestaurantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurant(restaurant: Restaurant)

    @Delete
    fun deleteRestaurant(restaurant: Restaurant)

    @Query("SELECT * from restaurants")
    fun getAll(): List<Restaurant>

    @Query("DELETE FROM restaurants")
    fun deleteAll()

}