package com.example.mobilemeals.adapters

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilemeals.BottomNavigationBarActivity
import com.example.mobilemeals.Database.AppDatabase
import com.example.mobilemeals.R
import com.example.mobilemeals.databinding.RestaurantRecyclerviewItemBinding
import com.example.mobilemeals.fragments.ViewDishesFragment
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.GetRestaurantRatingResponse
import com.example.mobilemeals.models.Restaurant
import kotlinx.android.synthetic.main.restaurant_recyclerview_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantAdapter (private val context: Context, private val restaurants: List<Restaurant>, private val favouriteRestaurants: List<Restaurant>): RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    val retrofitService = HelperMethods.service
    val not_fav = context.resources.getDrawable(R.drawable.not_fav)
    val fav = context.resources.getDrawable(R.drawable.favorite)
    val star = context.resources.getDrawable(R.drawable.star)
    val database = AppDatabase.getInstance(context)

    inner class RestaurantViewHolder(itemView: RestaurantRecyclerviewItemBinding):
        RecyclerView.ViewHolder(itemView.root) {
            fun bindRestaurant(restaurant: Restaurant) {
                itemView.restaurantName.text = restaurant.name
                itemView.restaurant_address.text = restaurant.address + ", " + restaurant.city
                itemView.workingHours.text = restaurant.open_time + " - " + restaurant.close_time
                itemView.cuisine.text = restaurant.cuisine
                Glide.with(context).load(restaurant.img_url).into(itemView.restaurantImageView)
                println("Fav: ${favouriteRestaurants.size}")
                val foundElement = favouriteRestaurants?.find {
                    it._id == restaurant._id
                }
                if(foundElement != null) {
                    itemView.favImgView.setImageDrawable(fav)
                }
                else {
                    itemView.favImgView.setImageDrawable(not_fav)
                }

                fetchRatings(restaurant._id, itemView.cardView.rating, itemView.cardView.ratingView)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = RestaurantRecyclerviewItemBinding.inflate(from, parent, false)
        var restaurantViewHolder = RestaurantViewHolder(binding)
        restaurantViewHolder.itemView.cardView.setOnClickListener{
            val viewDishes = ViewDishesFragment()
            val bundle = Bundle()
            bundle.putSerializable(ViewDishesFragment.RESTAURANT, restaurants[restaurantViewHolder.adapterPosition])
            viewDishes.arguments = bundle
            (context as BottomNavigationBarActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, viewDishes, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }

        restaurantViewHolder.itemView.favImgView.setOnClickListener {
            if(restaurantViewHolder.itemView.favImgView.drawable == not_fav) {
                restaurantViewHolder.itemView.favImgView.setImageDrawable(fav)
                AsyncTask.execute {
                    database?.restaurantDao()?.insertRestaurant(restaurants[restaurantViewHolder.adapterPosition])
                }

            }
            else {
                restaurantViewHolder.itemView.favImgView.setImageDrawable(not_fav)
                AsyncTask.execute {
                    database?.restaurantDao()?.deleteRestaurant(restaurants[restaurantViewHolder.adapterPosition])
                }

            }
        }
        return restaurantViewHolder
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bindRestaurant(restaurants.get(position))
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    private fun fetchRatings(restaurant_id: String, ratingTextView: TextView, ratingView: LinearLayout){
        val getratingCall = retrofitService.getRestaurantRating(restaurant_id)
        getratingCall.enqueue(object: Callback<GetRestaurantRatingResponse>{
            override fun onResponse(
                call: Call<GetRestaurantRatingResponse>,
                response: Response<GetRestaurantRatingResponse>
            ) {
                if(response.isSuccessful) {
                    if(response.body() != null) {
                        val ratings = response.body()!!.ratings
                        val sumRatings = ratings.map { it.rating }.sum()
                        val avgrating = sumRatings/ratings.size
                        if(avgrating.isNaN()) {
                            ratingView.visibility = View.INVISIBLE
                        }
                        else {
                            ratingView.visibility = View.VISIBLE
                            ratingTextView.text = String.format("%.1f", avgrating)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetRestaurantRatingResponse>, t: Throwable) {

            }

        })
    }
}