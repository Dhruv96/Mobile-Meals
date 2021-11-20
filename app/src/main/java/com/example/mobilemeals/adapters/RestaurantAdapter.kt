package com.example.mobilemeals.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilemeals.BottomNavigationBarActivity
import com.example.mobilemeals.R
import com.example.mobilemeals.databinding.RestaurantRecyclerviewItemBinding
import com.example.mobilemeals.fragments.ViewDishesFragment
import com.example.mobilemeals.models.Restaurant
import kotlinx.android.synthetic.main.restaurant_recyclerview_item.view.*

class RestaurantAdapter (private val context: Context, private val restaurants: List<Restaurant>): RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(itemView: RestaurantRecyclerviewItemBinding):
        RecyclerView.ViewHolder(itemView.root) {
            fun bindRestaurant(restaurant: Restaurant) {
                itemView.restaurantName.text = restaurant.name
                itemView.restaurant_address.text = restaurant.address + ", " + restaurant.city
                itemView.workingHours.text = restaurant.open_time + " - " + restaurant.close_time
                itemView.cuisine.text = restaurant.cuisine
                Glide.with(context).load(restaurant.img_url).into(itemView.restaurantImageView)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = RestaurantRecyclerviewItemBinding.inflate(from, parent, false)
        var restaurantViewHolder = RestaurantViewHolder(binding)
        restaurantViewHolder.itemView.cardView.setOnClickListener{
            val viewDishes = ViewDishesFragment()
            val bundle = Bundle()
            bundle.putSerializable(ViewDishesFragment.RESTAURANT, restaurants[viewType])
            viewDishes.arguments = bundle
            (context as BottomNavigationBarActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, viewDishes, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }
        return restaurantViewHolder
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bindRestaurant(restaurants.get(position))
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }
}