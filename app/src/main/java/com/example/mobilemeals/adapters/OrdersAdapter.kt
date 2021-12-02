package com.example.mobilemeals.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.mobilemeals.databinding.OrdersRecyclerviewItemBinding
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.GetSpecificRestaurantResponse
import com.example.mobilemeals.models.MealOrder
import kotlinx.android.synthetic.main.orders_recyclerview_item.view.*
import kotlinx.android.synthetic.main.orders_recyclerview_item.view.cardView
import kotlinx.android.synthetic.main.restaurant_recyclerview_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersAdapter (private val orders: List<MealOrder>, private val context: Context): RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(itemView: OrdersRecyclerviewItemBinding): RecyclerView.ViewHolder(itemView.root) {
       fun bindOrder(order: MealOrder) {
           itemView.cardView.orderString.text = order.item_string
           itemView.cardView.finalPrice.text = order.final_price.toString()
           fetchRestaurantNameAndImage(order, itemView.restaurantImg, itemView.restaurantNameOrders)
       }

        private fun fetchRestaurantNameAndImage(order: MealOrder, restaurantImgView: ImageView, restaurantName: TextView) {
            val retrofitService = HelperMethods.service
            val restaurantCall = retrofitService.getSpecificRestaurant(order.restaurant_id)
            restaurantCall.enqueue(object: Callback<GetSpecificRestaurantResponse> {
                override fun onResponse(
                    call: Call<GetSpecificRestaurantResponse>,
                    response: Response<GetSpecificRestaurantResponse>
                ) {
                    if(response.isSuccessful) {
                        if(response.body() != null) {
                            val restaurant = response.body()!!.restaurant
                            restaurantName.text = restaurant.name
                            Glide.with(context).load(restaurant.img_url).into(itemView.restaurantImg)
                        }
                    }
                }

                override fun onFailure(call: Call<GetSpecificRestaurantResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = OrdersRecyclerviewItemBinding.inflate(from, parent, false)
        val ordersViewHolder = OrdersViewHolder(binding)
        return ordersViewHolder
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bindOrder(orders[position])
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}