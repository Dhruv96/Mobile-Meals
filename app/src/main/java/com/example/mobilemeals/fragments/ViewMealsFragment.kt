package com.example.mobilemeals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilemeals.R
import com.example.mobilemeals.adapters.AdminMealAdapter
import com.example.mobilemeals.adapters.MealAdapter
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.Dish
import com.example.mobilemeals.models.GetDishesResponse
import com.example.mobilemeals.models.Restaurant
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_view_dishes.*
import kotlinx.android.synthetic.main.fragment_view_meals.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewMealsFragment : Fragment() {

    lateinit var restaurant: Restaurant

    companion object {
        val RESTAURANT = "restaurant"
    }
    val retrofitService = HelperMethods.service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_meals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
             restaurant = requireArguments().getSerializable(RESTAURANT) as Restaurant
              fetchMeals()
        }
    }

    private fun fetchMeals() {
        val getMealsCall = retrofitService.getDishes(restaurant._id)
        getMealsCall.enqueue(object: Callback<GetDishesResponse> {
            override fun onResponse(
                call: Call<GetDishesResponse>,
                response: Response<GetDishesResponse>
            ) {
                if(response.isSuccessful) {
                    if(response.body() != null) {
                        val dishes = response.body()!!.dishes as MutableList<Dish>
                        val adminMealAdapter = AdminMealAdapter(dishes, requireContext())
                        mealsRecyclerView.apply {
                            adapter = adminMealAdapter
                            layoutManager = LinearLayoutManager(requireContext())
                        }

                    }
                    else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(requireContext(), jObjError.getString("error"), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<GetDishesResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }


}