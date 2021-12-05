package com.example.mobilemeals.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilemeals.AdminActivity
import com.example.mobilemeals.BottomNavigationBarActivity
import com.example.mobilemeals.R
import com.example.mobilemeals.databinding.AdminRestaurantsRecyclerviewItemBinding
import com.example.mobilemeals.fragments.AddRestaurantFragment
import com.example.mobilemeals.fragments.ViewMealsFragment
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.Restaurant
import kotlinx.android.synthetic.main.admin_restaurants_recyclerview_item.view.*
import kotlinx.android.synthetic.main.admin_restaurants_recyclerview_item.view.cardView
import kotlinx.android.synthetic.main.admin_restaurants_recyclerview_item.view.restaurantImageView
import kotlinx.android.synthetic.main.admin_restaurants_recyclerview_item.view.restaurantName
import kotlinx.android.synthetic.main.admin_restaurants_recyclerview_item.view.restaurant_address
import kotlinx.android.synthetic.main.restaurant_recyclerview_item.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminRestaurantsAdapter(private val restaurants: MutableList<Restaurant>, private val context: Context) : RecyclerView.Adapter<AdminRestaurantsAdapter.AdminRestaurantViewHolder>() {

    val retrofitService = HelperMethods.service

    inner class AdminRestaurantViewHolder(itemView: AdminRestaurantsRecyclerviewItemBinding): RecyclerView.ViewHolder(itemView.root){
        fun bindRestaurant(restaurant: Restaurant) {
            itemView.cardView.restaurantName.text = restaurant.name
            itemView.cardView.restaurant_address.text = restaurant.address
            Glide.with(context).load(restaurant.img_url).into(itemView.restaurantImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRestaurantViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = AdminRestaurantsRecyclerviewItemBinding.inflate(from, parent, false)
        var adminRestaurantViewHolder = AdminRestaurantViewHolder(binding)
        adminRestaurantViewHolder.itemView.delete_restaurantButton.setOnClickListener {
            val deleteRestaurantCall = retrofitService.deleteRestaurant(restaurants[adminRestaurantViewHolder.adapterPosition]._id)
            deleteRestaurantCall.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                   if(response.isSuccessful) {
                       if(response.body() != null) {
                           val jsonObject = JSONObject(response.body()!!.string())
                           Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                           restaurants.removeAt(adminRestaurantViewHolder.adapterPosition)
                           notifyDataSetChanged()
                       }
                       else {
                           val jObjError = JSONObject(response.errorBody()!!.string())
                           println(response.errorBody().toString())
                           Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_LONG).show()
                       }
                   }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
                }

            })
        }

        adminRestaurantViewHolder.itemView.view_meals_button.setOnClickListener {
            val view_meals_fragment = ViewMealsFragment()
            val bundle = Bundle()
            bundle.putSerializable(ViewMealsFragment.RESTAURANT, restaurants[adminRestaurantViewHolder.absoluteAdapterPosition])
            view_meals_fragment.arguments = bundle
            (context as AdminActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, view_meals_fragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }

        adminRestaurantViewHolder.itemView.edit_restaurant_btn.setOnClickListener {
            val addRestaurantFragment = AddRestaurantFragment()
            val bundle = Bundle()
            bundle.putSerializable(AddRestaurantFragment.RESTAURANT, restaurants[adminRestaurantViewHolder.adapterPosition])
            addRestaurantFragment.arguments = bundle
            (context as AdminActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, addRestaurantFragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }
        return adminRestaurantViewHolder
    }

    override fun onBindViewHolder(holder: AdminRestaurantViewHolder, position: Int) {
        holder.bindRestaurant(restaurants[position])
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }
}