package com.example.mobilemeals.helpers

import android.app.Application
import android.app.TimePickerDialog
import android.content.Context
import android.location.Address
import android.widget.EditText
import android.widget.Toast
import com.example.mobilemeals.network.RetrofitService
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import com.google.android.gms.maps.model.LatLng
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import com.example.mobilemeals.BottomNavigationBarActivity
import com.example.mobilemeals.R
import com.example.mobilemeals.fragments.MapsFragment
import com.example.mobilemeals.models.BodyForPostingOrder
import java.io.InputStream
import java.lang.Exception


class HelperMethods {

    companion object
    {
        // Get retrofit service
        val service = Retrofit.Builder()
            .baseUrl(getIpAddress())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(RetrofitService::class.java)

        // reads the ip adress from txt file in assets and returns it
        fun getIpAddress(): String {
            var ipaddress = ""
            val context = GlobalApplication.getAppContext()
            try {
                val inputStream: InputStream = context.assets.open("source.txt")
                val size: Int = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                ipaddress = String(buffer)

            } catch (e: Exception) {
                Log.d("error", e.message.toString())
            }
            return ipaddress
        }


        // transform edittext into timepicker
        fun EditText.transformIntoTimePicker(context: Context) {
            isFocusableInTouchMode = false
            isClickable = true
            isFocusable = false

            // Get Current Time
            val c = Calendar.getInstance()
            var mHour = c[Calendar.HOUR_OF_DAY]
            var mMinute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog

            setOnClickListener{
                val timePickerDialog = TimePickerDialog(context,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        setText("")
                        mHour = hourOfDay
                        mMinute = minute
                        if (mHour < 10 && mMinute < 10) {
                            setText(text.toString() + " " + "0" + mHour + ":" + "0" + minute)
                        } else if (mHour < 10) {
                            setText(text.toString() + " " + "0" + mHour + ":" + minute)
                        } else if (mMinute < 10) {
                            setText(text.toString() + " " + mHour + ":" + "0" + minute)
                        } else {
                            setText(text.toString() + " " + mHour + ":" + minute)
                        }

                    }, mHour, mMinute, false)

                timePickerDialog.show()
            }

        }

        fun roundTo2decimal(number:Double): Double {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number).toDouble()
        }

        // api call to clear user's cart
        fun clearCartCall(userId: String, context: Context, myFunc: Unit){
            val service = service
            val clearCartCall = service.clearCart(userId)
            clearCartCall.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful){
                        if(response.body() != null) {
                            val jObjError = JSONObject(response.body()!!.string())
                            Toast.makeText(context, jObjError.getString("message"), Toast.LENGTH_LONG).show()
                            myFunc
                        }
                        else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
                }

            })
        }

        // get latitude and longitude for map from address
        fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
            print(strAddress)
            val coder = Geocoder(context)
            val address: List<Address>?
            var p1: LatLng? = null
            try {
                address = coder.getFromLocationName(strAddress, 5)
                if (address == null) {
                    println("address null")
                    return null
                }
                println("Size")
                println(address.size)
                val location: Address = address[0]
                location.latitude
                location.longitude
                p1 = LatLng(location.latitude, location.longitude)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            println("returning results: ${p1?.latitude}${p1?.longitude}")
            return p1
        }

        // open a fragment based on success response
        fun openOrderSuccess(context: Context, bodyForPostingOrder: BodyForPostingOrder) {
            val mapsFragment = MapsFragment()
            val bundle = Bundle()
            bundle.putSerializable(MapsFragment.ORDER, bodyForPostingOrder)
            mapsFragment.arguments = bundle
            (context as BottomNavigationBarActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapsFragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }
    }


}