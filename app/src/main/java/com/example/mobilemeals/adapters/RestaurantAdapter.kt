package com.example.mobilemeals.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilemeals.databinding.RestaurantRecyclerviewItemBinding
import com.example.mobilemeals.models.Restaurant
import kotlinx.android.synthetic.main.restaurant_recyclerview_item.view.*

class RestaurantAdapter (private val context: Context, private val restaurants: List<Restaurant>): RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(itemView: RestaurantRecyclerviewItemBinding):
        RecyclerView.ViewHolder(itemView.root) {
            fun bindRestaurant(restaurant: Restaurant) {
                itemView.restaurantName.text = restaurant.name
                itemView.workingHours.text = restaurant.open_time + " - " + restaurant.close_time
                itemView.cuisine.text = restaurant.cuisine
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = RestaurantRecyclerviewItemBinding.inflate(from, parent, false)
        var restaurantViewHolder = RestaurantViewHolder(binding)
        restaurantViewHolder.itemView.cardView.setOnClickListener{
            println("tapped")
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