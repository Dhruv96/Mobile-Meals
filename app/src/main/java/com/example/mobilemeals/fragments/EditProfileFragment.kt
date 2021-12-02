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
import com.example.mobilemeals.models.UpdateUserProfileRequest
import com.example.mobilemeals.models.UserLoginResponse
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {

    lateinit var mPrefs: SharedPreferences
    lateinit var user: UserLoginResponse
    val retrofitService = HelperMethods.service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPrefs = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = mPrefs.getString("USER", "")
        println(mPrefs.getBoolean("isLoggedIn", false))
        println(json)
        user = gson.fromJson(json, UserLoginResponse::class.java)
        nameTf.setText(user.name)
        emailTf.setText(user.email)
        addressTextField.setText(user.address)
        userCity.setText(user.city)
        update_profile_btn.setOnClickListener {
            val name = nameTf.text.toString().trim()
            val email = emailTf.text.toString().trim()
            val address = addressTextField.text.toString().trim()
            val city = userCity.text.toString().trim()
            val password = passwordTf.text.toString().trim()

            if(name != "" && email != "" && address != "" && city != "" && password != "") {
                val updateUserProfileRequest  = UpdateUserProfileRequest(user._id, email, name, address, city, password)
                val updateProfileCall = retrofitService.updateUserProfile(updateUserProfileRequest)
                updateProfileCall.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if(response.isSuccessful) {
                            if(response.body() != null) {
                                val jsonObject = JSONObject(response.body()!!.string())
                                Toast.makeText(requireContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                                val user = jsonObject.getJSONObject("user")

                                //val gson = Gson()
                                //val json = gson.toJson(user)
                                val json = user.toString()
                                val sharedPref = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE) ?: return
                                with (sharedPref.edit()) {
                                    putString("USER", json)
                                    apply()
                                }

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

//{"_id":"fbf8e0949e224ec59dba23d9eddacfac","address":"324 Blue Mountain St","city":"NW","email":"dg@gmail.com","name":"Dhruv"}
// {"_id":"fbf8e0949e224ec59dba23d9eddacfac","address":"324 Blue Mountain St","city":"Coquitlam","email":"dg@gmail.com","name":"Dhruv"}