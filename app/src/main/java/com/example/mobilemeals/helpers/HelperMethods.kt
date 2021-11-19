package com.example.mobilemeals.helpers

import android.app.TimePickerDialog
import android.content.Context
import android.widget.EditText
import com.example.mobilemeals.network.RetrofitService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class HelperMethods {
    companion object
    {
        val service = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(RetrofitService::class.java)


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
    }


}