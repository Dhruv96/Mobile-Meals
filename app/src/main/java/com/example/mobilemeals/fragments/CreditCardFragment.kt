package com.example.mobilemeals.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.braintreepayments.cardform.view.CardForm
import com.example.mobilemeals.R
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.BodyForPostingOrder
import com.example.mobilemeals.models.UserLoginResponse
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_credit_card.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreditCardFragment : Fragment() {
    val retrofitService = HelperMethods.service
    lateinit var bodyForPostingOrder: BodyForPostingOrder
    lateinit  var mPrefs: SharedPreferences
    lateinit var user: UserLoginResponse
    companion object {
        val ORDER = "order"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_credit_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            bodyForPostingOrder = requireArguments().getSerializable(ORDER) as BodyForPostingOrder
        }
        mPrefs = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = mPrefs.getString("USER", "")
        println(mPrefs.getBoolean("isLoggedIn", false))
        println(json)
        user = gson.fromJson(json, UserLoginResponse::class.java)
        card_form.cardRequired(true)
            .expirationRequired(true)
            .cvvRequired(true)
            .cardholderName(CardForm.FIELD_REQUIRED)
            .postalCodeRequired(true)
            .setup(activity);

        place_order_button.setOnClickListener {
            if (card_form.isValid && ::bodyForPostingOrder.isInitialized) {
                val postOrderCall = retrofitService.addNewOrder(bodyForPostingOrder)
                postOrderCall.enqueue(object: Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if(response.isSuccessful) {
                            if(response.body() != null) {
                                val jObjError = JSONObject(response.body()!!.string())
                                Toast.makeText(requireContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show()
                                HelperMethods.clearCartCall(user._id, requireContext(), openOrderSuccessPage())
                            }
                        }
                        else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            Toast.makeText(requireContext(), jObjError.getString("error"), Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_LONG).show()
                    }

                })
            }
            else {
                Toast.makeText(requireContext(), "Please enter all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun openOrderSuccessPage() {
        HelperMethods.openOrderSuccess(requireContext(), bodyForPostingOrder)
    }
}