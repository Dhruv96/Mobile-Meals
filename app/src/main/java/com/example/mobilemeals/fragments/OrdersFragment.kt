package com.example.mobilemeals.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilemeals.R
import com.example.mobilemeals.adapters.OrdersAdapter
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.UserLoginResponse
import com.example.mobilemeals.models.UserOrdersResponse
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_orders.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdersFragment : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserOrders()
    }

    // GETTING LOGGED IN USER'S ORDER
    private fun getUserOrders() {
        println("Fetching Orders")
        val mPrefs = requireContext().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = mPrefs.getString("USER", "")
        val user = gson.fromJson(json, UserLoginResponse::class.java)
        val retrofitService = HelperMethods.service
        val getUserOrdersCall = retrofitService.getUserOrders(user._id)
        getUserOrdersCall.enqueue(object: Callback<UserOrdersResponse> {
            override fun onResponse(
                call: Call<UserOrdersResponse>,
                response: Response<UserOrdersResponse>
            ) {
                println(response.body().toString())
                if(response.isSuccessful) {
                    println("******")
                    if(response.body() != null) {
                        val mealOrders = response.body()!!.orders
                        println(response.body()!!.orders)
                        if(mealOrders != null) {
                            val orderAdapter = OrdersAdapter(mealOrders.orders, requireContext())
                            ordersRecyclerView.apply {
                                adapter = orderAdapter
                                layoutManager = LinearLayoutManager(requireContext())
                            }
                        }

                    }
                    else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        println(response.errorBody().toString())
                        Toast.makeText(requireContext(), jObjError.getString("error"), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserOrdersResponse>, t: Throwable) {
                println(t.localizedMessage)
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })

    }
}