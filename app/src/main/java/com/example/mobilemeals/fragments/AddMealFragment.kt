package com.example.mobilemeals.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mobilemeals.R
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_add_meal.*
import kotlinx.android.synthetic.main.fragment_add_restaurant.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class AddMealFragment : Fragment() {

    lateinit var fileUrl: Uri
    val storageRef = Firebase.storage.reference
    var restaurants = mutableListOf<Restaurant>()
    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            mealImgView.setImageURI(uri)
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
        return inflater.inflate(R.layout.fragment_add_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAllRestaurants()
        mealImgView.setOnClickListener {
            selectImageFromGallery()
        }
        submit_btn.setOnClickListener {
            val dishName = dishNameTf.text.toString().trim()
            val dishCategory = dishCategory.text.toString().trim()
            val dishPrice = dishPrice.text.toString().trim()
            val restaurantId = restaurants[restaurant_spinner.selectedItemPosition]._id
            if (!this::fileUrl.isInitialized) {
                Toast.makeText(requireContext(), "Please upload the dish image!", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                val uuid = UUID.randomUUID()
                val dishRef = storageRef.child("dishImages/${uuid}")
                val uploadTask = dishRef.putFile(fileUrl)
                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                        val downloadUrl = task.result!!.toString()
                        val dish = Dish("", dishName, dishCategory, dishPrice, restaurantId, downloadUrl)
                        val retrofitService = HelperMethods.service
                        val addNewDishCall = retrofitService.addDish(dish)
                        addNewDishCall.enqueue(object: Callback<AddNewDishResponse> {
                            override fun onResponse(
                                call: Call<AddNewDishResponse>,
                                response: Response<AddNewDishResponse>
                            ) {
                                if(response.isSuccessful) {
                                    if(response.body() != null) {
                                        val name = response.body()?.dish?.name
                                        Toast.makeText(requireContext(), "${name} added", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else {
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    Toast.makeText(requireContext(), jObjError.getString("error"), Toast.LENGTH_LONG).show()

                                }
                            }

                            override fun onFailure(
                                call: Call<AddNewDishResponse>,
                                t: Throwable
                            ) {
                                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                            }

                        })
                    }

                }
            }
        }
    }

    private fun fetchAllRestaurants() {
        val retrofitService = HelperMethods.service
        val fetchRestaurantsCall = retrofitService.getAllRestaurants()
        fetchRestaurantsCall.enqueue(object: Callback<GetAllRestaurantsResponse>{
            override fun onResponse(
                call: Call<GetAllRestaurantsResponse>,
                response: Response<GetAllRestaurantsResponse>
            ) {
                if(response.isSuccessful) {
                    if(response.body() != null) {
                        restaurants.clear()
                        restaurants = response.body()!!.restaurants as MutableList<Restaurant>
                        val restaurant_names = Array(restaurants.size) { i -> restaurants.get(i).name }
                        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, restaurant_names)
                        restaurant_spinner.adapter = arrayAdapter
                    }
                    else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(requireContext(), jObjError.getString("error"), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<GetAllRestaurantsResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

}