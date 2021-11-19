package com.example.mobilemeals

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.User
import com.example.mobilemeals.models.UserLoginResponse
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {


    override fun onStart() {
        super.onStart()
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        if(sharedPref.getBoolean("isLoggedIn",false)) {
            val intent = Intent(this@LoginActivity, BottomNavigationBarActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val service = HelperMethods.service

        sign_up.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        admin_login_btn.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailTextField.text.toString()
            val password = passwordTextField.text.toString()

            if(email != "" && password != "") {
                val user = User(email, password)
                val signInCall = service.signIn(user)
                signInCall.enqueue(object: Callback<UserLoginResponse> {
                    override fun onResponse(
                        call: Call<UserLoginResponse>,
                        response: Response<UserLoginResponse>
                    ) {
                        if(response.isSuccessful) {
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            val sharedPref = this@LoginActivity.getPreferences(Context.MODE_PRIVATE) ?: return
                            with (sharedPref.edit()) {
                                putBoolean("isLoggedIn", true)
                                apply()
                            }
                            val intent = Intent(this@LoginActivity, BottomNavigationBarActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            Toast.makeText(this@LoginActivity, jObjError.getString("error"), Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                    }

                })

            }

        }

    }


}