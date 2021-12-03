package com.example.mobilemeals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilemeals.R
import com.example.mobilemeals.adapters.AdminRestaurantsAdapter
import com.example.mobilemeals.adapters.RestaurantAdapter
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.GetAllRestaurantsResponse
import com.example.mobilemeals.models.Restaurant
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_view_restaurants.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewRestaurantsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_restaurants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAllRestaurants()
    }

    private fun getAllRestaurants() {
        val getRestaurantsCall = HelperMethods.service.getAllRestaurants()
        getRestaurantsCall.enqueue(object: Callback<GetAllRestaurantsResponse> {
            override fun onResponse(
                call: Call<GetAllRestaurantsResponse>,
                response: Response<GetAllRestaurantsResponse>
            ) {
                if(response.isSuccessful) {
                    if(response.body() != null) {
                        val restaurants = response.body()!!.restaurants
                        val adminRestaurantsAdapter = AdminRestaurantsAdapter(restaurants as MutableList<Restaurant>, requireContext())
                        adminRestaurantsRecyclerView.apply {
                            adapter = adminRestaurantsAdapter
                            layoutManager = LinearLayoutManager(requireContext())
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