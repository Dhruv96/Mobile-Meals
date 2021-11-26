package com.example.mobilemeals.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilemeals.databinding.MealRecyclerviewItemBinding
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.CartItem
import com.example.mobilemeals.models.Dish
import com.example.mobilemeals.models.UserCartItem
import com.example.mobilemeals.models.UserLoginResponse
import com.google.gson.Gson
import kotlinx.android.synthetic.main.meal_recyclerview_item.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class MealAdapter (private val dishes: List<Dish>, private val context: Context):
    RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    val retrofitService = HelperMethods.service
    val mPrefs = context.getSharedPreferences("myPref", MODE_PRIVATE)
    val gson = Gson()
    val json = mPrefs.getString("USER", "")
    val user = gson.fromJson(json, UserLoginResponse::class.java)

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
        mealViewHolder.itemView.addToCartButton.setOnClickListener {
            val cartItem = CartItem(dishes[mealViewHolder.adapterPosition]._id, 1)
            val userCartItem = UserCartItem(user._id, cartItem)
            val addItemCall = retrofitService.addItemToCart(userCartItem)
            addItemCall.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful) {
                        if (response.code() == 200) {
                            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_LONG).show()

                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }

            })
            it.visibility = View.INVISIBLE
        }


        mealViewHolder.itemView.add.setOnClickListener {
            var currentQuantityString = mealViewHolder.itemView.quantity.text.toString()
            var currentQuantityInt = Integer.parseInt(currentQuantityString)
            currentQuantityInt += 1
            mealViewHolder.itemView.quantity.text = (currentQuantityInt).toString()
            val cartItem = CartItem(dishes[mealViewHolder.adapterPosition]._id, currentQuantityInt)
            val userCartItem = UserCartItem(user._id, cartItem)
            updateCart(userCartItem)

        }

        mealViewHolder.itemView.subtract.setOnClickListener {
            var currentQuantityString = mealViewHolder.itemView.quantity.text.toString()
            var currentQuantityInt = Integer.parseInt(currentQuantityString)
            if(currentQuantityInt > 1) {
                currentQuantityInt -= 1
                mealViewHolder.itemView.quantity.text = (currentQuantityInt).toString()
                val cartItem = CartItem(dishes[viewType]._id, currentQuantityInt)
                val userCartItem = UserCartItem(user._id, cartItem)
                updateCart(userCartItem)
            }

        }
        return mealViewHolder
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.binMeal(dishes[position])
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    private fun updateCart(userCartItem: UserCartItem) {
        val updateCartCall = retrofitService.updateCartItem(userCartItem)
        updateCartCall.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful) {
                    if (response.code() == 200) {
                        Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_LONG).show()

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }
}