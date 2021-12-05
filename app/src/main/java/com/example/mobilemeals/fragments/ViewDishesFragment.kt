package com.example.mobilemeals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilemeals.R
import com.example.mobilemeals.adapters.MealAdapter
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.Dish
import com.example.mobilemeals.models.GetDishesResponse
import com.example.mobilemeals.models.Restaurant
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_view_dishes.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ViewDishesFragment : Fragment() {

    lateinit var restaurant: Restaurant
    var dishes = mutableListOf<Dish>()
    var categories = mutableSetOf<String>()
    companion object {
        val RESTAURANT = "restaurant"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_dishes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            restaurant = requireArguments().getSerializable(RESTAURANT) as Restaurant
            fetchDishes()
            tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val filteredDishes = dishes.filter {
                         it.category == tab?.text
                    }

                    dishesRecyclerView.apply {
                        val mealAdapter = MealAdapter(filteredDishes, requireContext())
                        adapter = mealAdapter
                        layoutManager = LinearLayoutManager(requireContext())
                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })
        }
    }

    // Getting dishes of specific restaurant
    private fun fetchDishes() {
        val retrofitService = HelperMethods.service
        val fetchDishesCall = retrofitService.getDishes(restaurant._id)
        fetchDishesCall.enqueue(object: Callback<GetDishesResponse> {
            override fun onResponse(
                call: Call<GetDishesResponse>,
                response: Response<GetDishesResponse>
            ) {
                if(response.isSuccessful) {
                    if(response.body() != null) {
                        dishes = response.body()!!.dishes as MutableList<Dish>
                        dishes.forEach {
                            categories.add(it.category)
                        }

                        tabLayout.removeAllTabs()
                        categories.forEach {
                            tabLayout.addTab(tabLayout.newTab().setText(it))
                        }
                        val mealAdapter = MealAdapter(dishes.filter {
                                  it.category == categories.elementAt(0)
                        }, requireContext())
                        dishesRecyclerView.apply {
                            adapter = mealAdapter
                            layoutManager = LinearLayoutManager(requireContext())
                        }
                        if(tabLayout.tabCount <4)
                        {
                            tabLayout.tabGravity = TabLayout.GRAVITY_FILL;
                        }else
                        {
                            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE;

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