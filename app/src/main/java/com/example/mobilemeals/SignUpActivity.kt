package com.example.mobilemeals

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.UserSignUp
import com.example.mobilemeals.models.UserSignUpResponse
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject
import java.security.AccessController.getContext


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signup_button.setOnClickListener {
            val name = nameTf.text.toString()
            val email = emailTf.text.toString()
            val password = passwordTf.text.toString()

            if(email != "" && password != "" && name != "") {
                val userSignup = UserSignUp(name, email, password)
                val service = HelperMethods.service
                val userSignUpCall = service.signUp(userSignup)
                userSignUpCall.enqueue(object: Callback<UserSignUpResponse> {
                    override fun onResponse(
                        call: Call<UserSignUpResponse>,
                        response: Response<UserSignUpResponse>
                    ) {
                        if(response.isSuccessful) {
                            if(response.body() != null) {
                                println(response.body().toString())
                                Toast.makeText(this@SignUpActivity, "Sign up successful", Toast.LENGTH_SHORT).show()
                                val sharedPref = this@SignUpActivity.getPreferences(Context.MODE_PRIVATE) ?: return
                                with (sharedPref.edit()) {
                                    putBoolean("isLoggedIn", true)
                                    apply()
                                }
                                val intent = Intent(this@SignUpActivity, BottomNavigationBarActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        else {
                            println("Error")
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            Toast.makeText(this@SignUpActivity, jObjError.getString("error"), Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<UserSignUpResponse>, t: Throwable) {
                        Toast.makeText(this@SignUpActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                    }

                })
            }
            else {
                Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            }

        }
    }
}