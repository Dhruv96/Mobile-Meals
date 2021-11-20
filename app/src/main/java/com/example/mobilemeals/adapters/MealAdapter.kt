package com.example.mobilemeals.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilemeals.BottomNavigationBarActivity
import com.example.mobilemeals.R
import com.example.mobilemeals.databinding.MealRecyclerviewItemBinding
import com.example.mobilemeals.databinding.RestaurantRecyclerviewItemBinding
import com.example.mobilemeals.fragments.ViewDishesFragment
import com.example.mobilemeals.models.Dish
import kotlinx.android.synthetic.main.meal_recyclerview_item.view.*
import kotlinx.android.synthetic.main.restaurant_recyclerview_item.view.*

class MealAdapter (private val dishes: List<Dish>, private val context: Context):
    RecyclerView.Adapter<MealAdapter.MealViewHolder>() {


    inner class MealViewHolder(itemView: MealRecyclerviewItemBinding): RecyclerView.ViewHolder(itemView.root) {
        fun binMeal(dish: Dish) {
            itemView.mealName.text = dish.name
            itemView.mealPrice.text = dish.price
            Glide.with(context).load(dish.img_url).into(itemView.mealImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = MealRecyclerviewItemBinding.inflate(from, parent, false)
        var mealViewHolder = MealViewHolder(binding)
        return mealViewHolder
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.binMeal(dishes[position])
    }

    override fun getItemCount(): Int {
        return dishes.size
    }
}