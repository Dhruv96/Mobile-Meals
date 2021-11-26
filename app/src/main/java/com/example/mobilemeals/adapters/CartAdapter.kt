package com.example.mobilemeals.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilemeals.databinding.CartRecyclerViewItemBinding
import com.example.mobilemeals.databinding.MealRecyclerviewItemBinding
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.CartItem
import com.example.mobilemeals.models.Dish
import com.example.mobilemeals.models.UserLoginResponse
import com.google.gson.Gson
import kotlinx.android.synthetic.main.cart_recycler_view_item.view.*
import kotlinx.android.synthetic.main.meal_recyclerview_item.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class CartAdapter (private val cartItems: MutableList<CartItem>, private val dishes: MutableList<Dish>, private val context: Context): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    interface EventListener {
        fun onDeletion()
    }
    constructor(cartItems: MutableList<CartItem>, dishes: MutableList<Dish>, context: Context, eventListener: EventListener):this(cartItems, dishes, context) {
        listener = eventListener
    }

    var listener: EventListener? = null

    val retrofitService = HelperMethods.service
    val mPrefs = context.getSharedPreferences("myPref", MODE_PRIVATE)
    val gson = Gson()
    val json = mPrefs.getString("USER", "")
    val user = gson.fromJson(json, UserLoginResponse::class.java)

    inner class CartViewHolder (itemView: CartRecyclerViewItemBinding): RecyclerView.ViewHolder(itemView.root){
        fun bindCartItem(cartItem: CartItem, dish: Dish){
            itemView.dishName.text = dish.name
            itemView.quantity_price.text = cartItem.quantity.toString() + " X " + dish.price
            Glide.with(context).load(dish.img_url).into(itemView.dishImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CartRecyclerViewItemBinding.inflate(from, parent, false)
        var cartViewHolder = CartViewHolder(binding)
        cartViewHolder.itemView.del_item_button.setOnClickListener {
            val delCartItemCall = retrofitService.deleteCartItem(user._id, cartItems[viewType].itemId)
            delCartItemCall.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful) {
                        if(response.body() != null) {
                            dishes.removeAt(cartViewHolder.adapterPosition)
                            cartItems.removeAt(cartViewHolder.adapterPosition)
                            notifyDataSetChanged()
                            listener?.onDeletion()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
                }

            })
        }
        return cartViewHolder
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bindCartItem(cartItems.get(position), dishes.get(position))
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}