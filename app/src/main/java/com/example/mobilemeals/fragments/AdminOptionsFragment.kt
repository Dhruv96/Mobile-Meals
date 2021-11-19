package com.example.mobilemeals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mobilemeals.R
import kotlinx.android.synthetic.main.fragment_admin_options.*


class AdminOptionsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add_restaurant.setOnClickListener {
           it.findNavController().navigate(R.id.action_adminOptionsFragment_to_addRestaurantFragment)
        }

        add_meal.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminOptionsFragment_to_addMealFragment)
        }

        view_meals.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminOptionsFragment_to_viewMealsFragment)
        }

        view_restaurants.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminOptionsFragment_to_viewRestaurantsFragment)
        }
    }

}