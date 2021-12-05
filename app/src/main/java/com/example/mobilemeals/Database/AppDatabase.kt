package com.example.mobilemeals.Database

import android.content.Context
import androidx.room.*
import com.example.mobilemeals.models.Restaurant

// class to get database instance
@Database(entities = [Restaurant::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun restaurantDao(): RestaurantDao?

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "FavRestaurants.db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}