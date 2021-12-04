package com.example.mobilemeals.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mobilemeals.R
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.UserLoginResponse
import com.example.mobilemeals.models.UserRating
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_rating.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class RatingFragment : Fragment() {

    lateinit var user: UserLoginResponse
    lateinit  var mPrefs: SharedPreferences
    lateinit var restaurantId: String
    val retrofitService = HelperMethods.service
    companion object {
        val RESTAURANT_ID = "restaurant_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            mPrefs = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = mPrefs.getString("USER", "")
            println(mPrefs.getBoolean("isLoggedIn", false))
            println(json)
            user = gson.fromJson(json, UserLoginResponse::class.java)
            restaurantId = requireArguments().getString(RESTAURANT_ID) as String
            submit_rating_btn.setOnClickListener {
                val rating = ratingBar.rating
                val userFeedback = feedbackEditText.text.toString().trim()
                if(userFeedback != "") {
                    val userRating = UserRating("", user._id, rating, userFeedback, restaurantId)
                    val submit_rating_call = retrofitService.addNewRating(userRating)
                    submit_rating_call.enqueue(object: retrofit2.Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if(response.isSuccessful) {
                                if(response.body() != null) {
                                    val jsonObject = JSONObject(response.body()!!.string())
                                    Toast.makeText(requireContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                                }
                                else {
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    Toast.makeText(requireContext(), jObjError.getString("error"), Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_LONG).show()
                        }

                    })
                }
            }

        }
    }

}