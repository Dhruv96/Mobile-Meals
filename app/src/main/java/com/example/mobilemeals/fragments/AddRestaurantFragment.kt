package com.example.mobilemeals.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.mobilemeals.R
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.helpers.HelperMethods.Companion.transformIntoTimePicker
import com.example.mobilemeals.models.AddNewRestaurantResponse
import com.example.mobilemeals.models.Restaurant
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_add_meal.*
import kotlinx.android.synthetic.main.fragment_add_restaurant.*
import kotlinx.android.synthetic.main.fragment_add_restaurant.restaurant_address
import kotlinx.android.synthetic.main.fragment_admin_options.*
import kotlinx.android.synthetic.main.restaurant_recyclerview_item.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


// This fragment is opened when admin tries to add or edit any restaurant
class AddRestaurantFragment : Fragment() {
    lateinit var restaurant: Restaurant
    companion object {
        val RESTAURANT = "restaurant"
    }

    lateinit var fileUrl: Uri
    val storageRef = Firebase.storage.reference

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            restaurantImgView.setImageURI(uri)
            fileUrl = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_restaurant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            // setting previous passed object details
            restaurant = requireArguments().getSerializable(RESTAURANT) as Restaurant
            restaurant_name.setText(restaurant.name)
            restaurant_address.setText(restaurant.address)
            restaurant_cuisine.setText(restaurant.cuisine)
            restaurant_opening_hour.setText(restaurant.open_time)
            restaurant_closing_hour.setText(restaurant.close_time)
            resturant_city.setText(restaurant.city)
            Glide.with(requireContext()).load(restaurant.img_url).into(restaurantImgView)
            submit_button.text = "Edit Restaurant"
        }

        restaurant_opening_hour.transformIntoTimePicker(requireContext())
        restaurant_closing_hour.transformIntoTimePicker(requireContext())
        restaurantImgView.setOnClickListener {
            selectImageFromGallery()
        }

        submit_button.setOnClickListener {
            if (submit_button.text != "Edit Restaurant") {
                val rest_name = restaurant_name.text.toString().trim()
                val address = restaurant_address.text.toString().trim()
                val city = resturant_city.text.toString().trim()
                val cuisine = restaurant_cuisine.text.toString().trim()
                val openingTime = restaurant_opening_hour.text.toString()
                val closingTime = restaurant_closing_hour.text.toString()
                if (!this::fileUrl.isInitialized) {
                    Toast.makeText(
                        requireContext(),
                        "Please upload the restaurant image!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    val uuid = UUID.randomUUID()
                    val restaurantRef = storageRef.child("restaurantImages/${uuid}")
                    val uploadTask = restaurantRef.putFile(fileUrl)
                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                        Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }.addOnSuccessListener { taskSnapshot ->
                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                            val downloadUrl = task.result!!.toString()
                            val restaurant = Restaurant(
                                "",
                                rest_name,
                                address,
                                city,
                                cuisine,
                                openingTime,
                                closingTime,
                                downloadUrl
                            )
                            val retrofitService = HelperMethods.service
                            val addNewRestaurantCall = retrofitService.addRestaurant(restaurant)
                            addNewRestaurantCall.enqueue(object :
                                Callback<AddNewRestaurantResponse> {
                                override fun onResponse(
                                    call: Call<AddNewRestaurantResponse>,
                                    response: Response<AddNewRestaurantResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        if (response.body() != null) {
                                            val name = response.body()?.restaurant?.name
                                            Toast.makeText(
                                                requireContext(),
                                                "${name} added",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            fileUrl = Uri.EMPTY
                                            restaurant_name.setText("")
                                            restaurant_address.setText("")
                                            restaurant_cuisine.setText("")
                                            restaurant_opening_hour.setText("")
                                            restaurant_closing_hour.setText("")
                                            resturant_city.setText("")
                                        }
                                    } else {
                                        val jObjError = JSONObject(response.errorBody()!!.string())
                                        Toast.makeText(
                                            requireContext(),
                                            jObjError.getString("error"),
                                            Toast.LENGTH_LONG
                                        ).show()

                                    }
                                }

                                override fun onFailure(
                                    call: Call<AddNewRestaurantResponse>,
                                    t: Throwable
                                ) {
                                    Toast.makeText(
                                        requireContext(),
                                        t.localizedMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            })
                        }

                    }
                }
            } else {
                var img_url = restaurant.img_url
                val restaurantName = restaurant_name.text.toString().trim()
                val restaurant_cuisine = restaurant_cuisine.text.toString().trim()
                val restaurant_city = resturant_city.text.toString().trim()
                val restaurant_openingHrs = restaurant_opening_hour.text.toString().trim()
                val restaurant_closingHour = restaurant_closing_hour.text.toString().trim()
                val restarant_address = restaurant_address.text.toString().trim()
                if (::fileUrl.isInitialized) {
                    val uuid = UUID.randomUUID()
                    val restaurantRef = storageRef.child("restaurantImages/${uuid}")
                    val uploadTask = restaurantRef.putFile(fileUrl)
                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                        Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }.addOnSuccessListener { taskSnapshot ->
                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                            img_url = task.result!!.toString()
                            val restaurant = Restaurant(
                                "",
                                restaurantName,
                                restarant_address,
                                restaurant_city,
                                restaurant_cuisine,
                                restaurant_openingHrs,
                                restaurant_closingHour,
                                img_url
                            )
                            updateRestaurant(restaurant)
                        }
                    }
                } else {
                    val restaurant = Restaurant(
                        "",
                        restaurantName,
                        restarant_address,
                        restaurant_city,
                        restaurant_cuisine,
                        restaurant_openingHrs,
                        restaurant_closingHour,
                        restaurant.img_url
                    )
                    updateRestaurant(restaurant)
                }
            }
        }
    }

    private fun updateRestaurant(updatedRestaurant: Restaurant) {
        val retrofitService = HelperMethods.service
        val updaterestaurantCall = retrofitService.editRestaurant(restaurant._id, updatedRestaurant)
        updaterestaurantCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        Toast.makeText(
                            requireContext(),
                            jsonObject.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            requireContext(),
                            jObjError.getString("error"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })
    }


    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

}