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
import com.example.mobilemeals.databinding.AdminViewMealRecyclerviewItemBinding
import com.example.mobilemeals.fragments.AddMealFragment
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.Dish
import kotlinx.android.synthetic.main.admin_view_meal_recyclerview_item.view.*
import kotlinx.android.synthetic.main.restaurant_recyclerview_item.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class AdminMealAdapter(private val dishes: MutableList<Dish>, private val context: Context): RecyclerView.Adapter<AdminMealAdapter.AdminMealViewHolder>() {

    inner class AdminMealViewHolder(itemView: AdminViewMealRecyclerviewItemBinding): RecyclerView.ViewHolder(itemView.root) {
        fun bindDish(dish: Dish) {
            itemView.dishNameAdmin.text = dish.name
            itemView.dishPriceAdmin.text = "$ ${dish.price}"
            Glide.with(context).load(dish.img_url).into(itemView.dishImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminMealViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = AdminViewMealRecyclerviewItemBinding.inflate(from, parent, false)
        var adminMealViewHolder = AdminMealViewHolder(binding)
        adminMealViewHolder.itemView.deleteMealBtn.setOnClickListener {
            val retrofitService = HelperMethods.service
            val deleteDishCall = retrofitService.deleteDish(dishes[adminMealViewHolder.adapterPosition]._id)
            deleteDishCall.enqueue(object: retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful) {
                        if(response.body() != null) {
                            val jsonObject = JSONObject(response.body()!!.string())
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                            dishes.removeAt(adminMealViewHolder.adapterPosition)
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

        adminMealViewHolder.itemView.editMeal.setOnClickListener {
            val addMealFragment = AddMealFragment()
            val bundle = Bundle()
            bundle.putSerializable(AddMealFragment.EDIT_MEAL, dishes[adminMealViewHolder.adapterPosition])
            addMealFragment.arguments = bundle
            (context as AdminActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, addMealFragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }

        return adminMealViewHolder
    }

    override fun onBindViewHolder(holder: AdminMealViewHolder, position: Int) {
        holder.bindDish(dishes[position])
    }

    override fun getItemCount(): Int {
        return dishes.size
    }
}