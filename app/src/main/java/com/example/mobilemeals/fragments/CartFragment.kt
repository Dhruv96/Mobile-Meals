package com.example.mobilemeals.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mobilemeals.R
import com.google.gson.Gson
import android.content.Context.MODE_PRIVATE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilemeals.adapters.CartAdapter
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.helpers.HelperMethods.Companion.roundTo2decimal
import com.example.mobilemeals.models.*
import kotlinx.android.synthetic.main.fragment_cart.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class CartFragment : Fragment(), CartAdapter.EventListener {

    lateinit  var mPrefs: SharedPreferences
    val retrofitService = HelperMethods.service
    lateinit var cartItems: CartItemsWithId
    var mealTotal = 0.0
    var taxes = 0.0
    var grandTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPrefs = requireActivity().getSharedPreferences("myPref", MODE_PRIVATE)
        val gson = Gson()
        val json = mPrefs.getString("USER", "")
        println(mPrefs.getBoolean("isLoggedIn", false))
        println(json)
        val user = gson.fromJson(json, UserLoginResponse::class.java)
        val cartCall = retrofitService.getUserCart(user._id)
        cartCall.enqueue(object: Callback<CartResponse>{
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if(response.isSuccessful) {
                    if(response.body() != null) {
                        cartItems = response.body()!!.cart
                        getCartDetails()
                    }
                    else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(requireContext(), jObjError.getString("error"), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getCartDetails() {
        mealTotal = 0.0
        taxes = 0.0
        grandTotal = 0.0
        val cartItems = cartItems.items
        val allItemIds = cartItems.map { cartItem -> cartItem.itemId}
        val cartDishesCall = retrofitService.getSpecificDishes(allItemIds)
        cartDishesCall.enqueue(object: Callback<GetSpecificDishesResponse>{
            override fun onResponse(
                call: Call<GetSpecificDishesResponse>,
                response: Response<GetSpecificDishesResponse>
            ) {
                if(response.isSuccessful) {
                    if(response.body() != null) {
                        val dishes = response.body()!!.dishes as MutableList<Dish>
                        println("Before modifying dish size: ${dishes.size}")
                        if(dishes.size != cartItems.size) {
                            for(i in dishes.size until cartItems.size) {
                                val index = dishes.indexOfFirst { dish -> dish._id == cartItems[i].itemId }
                                if(index >= 0) {
                                    dishes.add(dishes[index])
                                }
                            }
                        }
                        println(cartItems.size)
                        println(dishes.size)
                        val cart_adapter = CartAdapter(cartItems as MutableList<CartItem>,
                            dishes, requireContext(), this@CartFragment)
                        cartRecyclerView.apply {
                            adapter = cart_adapter
                            layoutManager = LinearLayoutManager(requireContext())
                        }
                        for (i in dishes.indices) {
                            mealTotal += cartItems[i].quantity.toDouble() * dishes[i].price.toDouble()
                        }
                        mealTotal = roundTo2decimal(mealTotal)
                        println(mealTotal)
                        taxes = 0.12*mealTotal
                        taxes = roundTo2decimal(taxes)
                        grandTotal = taxes + mealTotal
                        grandTotal = roundTo2decimal(grandTotal)
                        mealTotalTf.text = "MEAL TOTAL: $" + String.format("%.2f", mealTotal)
                        taxTotaltf.text = "TAXES: $" + String.format("%.2f", taxes)
                        grandTotaltf.text = "GRAND TOTAL: $" + String.format("%.2f", grandTotal)
                    }

                }
            }

            override fun onFailure(call: Call<GetSpecificDishesResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onDeletion() {
        println("triggered")
        getCartDetails()
    }
}