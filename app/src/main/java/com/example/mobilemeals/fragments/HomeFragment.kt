package com.example.mobilemeals.fragments

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilemeals.Database.AppDatabase
import com.example.mobilemeals.R
import com.example.mobilemeals.adapters.RestaurantAdapter
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.GetAllRestaurantsResponse
import com.example.mobilemeals.models.Restaurant
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// This fragment is opened when user logs in
class HomeFragment : Fragment() {

    var favouriteRestaurants: List<Restaurant> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFavouriteRestaurants()
    }

    // FETCHING ALL FAVORITE RESTAURANTS FROM SQLITE
    private fun getFavouriteRestaurants() {
        AsyncTask.execute { // Insert Data
            val database = AppDatabase.getInstance(requireContext())
            favouriteRestaurants = database?.restaurantDao()?.getAll() ?: listOf()
            getAllRestaurants()
        }
    }

    // FETCHING ALL RESTAURANTS FROM MONGODB
    private fun getAllRestaurants() {
        val getRestaurantsCall = HelperMethods.service.getAllRestaurants()
        getRestaurantsCall.enqueue(object: Callback<GetAllRestaurantsResponse>{
            override fun onResponse(
                call: Call<GetAllRestaurantsResponse>,
                response: Response<GetAllRestaurantsResponse>
            ) {
                if(response.isSuccessful) {
                    if(response.body() != null) {
                        println(response.body()!!.message)
                        val restaurantsAdapter = RestaurantAdapter(requireActivity(), response.body()!!.restaurants, favouriteRestaurants!!)
                        restaurantRecyclerView.apply {
                            adapter = restaurantsAdapter
                            layoutManager = LinearLayoutManager(requireActivity())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetAllRestaurantsResponse>, t: Throwable) {
                Toast.makeText(activity, t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }
}