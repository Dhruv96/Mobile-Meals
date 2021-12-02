package com.example.mobilemeals.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mobilemeals.R
import com.google.gson.Gson
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilemeals.BottomNavigationBarActivity
import com.example.mobilemeals.adapters.CartAdapter
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.helpers.HelperMethods.Companion.roundTo2decimal
import com.example.mobilemeals.models.*
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.*
import kotlinx.android.synthetic.main.fragment_cart.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CartFragment : Fragment(), CartAdapter.EventListener {
    private val YOUR_CLIENT_ID = "AaycaMU0AYrl6Sotrj78RaBB2MdGPs6Lajf5ynnYA8HDbgSe4i5uhTxuKIQ81yhiJ_W21QhDxdlkmXUN"
    lateinit  var mPrefs: SharedPreferences
    val retrofitService = HelperMethods.service
    lateinit var cartItems: CartItemsWithId
    var mealTotal = 0.0
    var taxes = 0.0
    var grandTotal = 0.0
    lateinit var restaurantID: String
    var itemString = ""
    lateinit var user: UserLoginResponse
    lateinit var bodyForPostingOrder: BodyForPostingOrder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemString = ""
        mPrefs = requireActivity().getSharedPreferences("myPref", MODE_PRIVATE)
        val gson = Gson()
        val json = mPrefs.getString("USER", "")
        println(mPrefs.getBoolean("isLoggedIn", false))
        println(json)
        user = gson.fromJson(json, UserLoginResponse::class.java)
        val cartCall = retrofitService.getUserCart(user._id)
        cartCall.enqueue(object: Callback<CartResponse>{
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if(response.isSuccessful) {
                    if(response.body() != null) {
                        val cart = response.body()!!.cart
                        if(cart != null) {
                            cartItems = response.body()!!.cart!!
                            getCartDetails()
                            if(cartItems.items.size > 0) {
                                cart_details_view.visibility = View.VISIBLE
                            }
                            else{
                                cart_details_view.visibility = View.INVISIBLE
                            }

                        }
                        else {
                            Toast.makeText(requireContext(), "Empty Cart", Toast.LENGTH_SHORT).show()
                            cart_details_view.visibility = View.INVISIBLE
                        }
                    }
                    else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(requireContext(), jObjError.getString("error"), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun setupPaypal() {
        val config = CheckoutConfig(
            application = requireActivity().application,
            clientId = YOUR_CLIENT_ID,
            environment = Environment.SANDBOX,
            returnUrl = "${com.example.mobilemeals.BuildConfig.APPLICATION_ID}://paypalpay",
            currencyCode = CurrencyCode.CAD,
            userAction = UserAction.PAY_NOW,

            settingsConfig = SettingsConfig(
                loggingEnabled = false
            )
        )
        PayPalCheckout.setConfig(config)

        buttonPayPaypal.setup(
            createOrder = CreateOrder { createOrderActions ->
                val order = Order(
                    intent = OrderIntent.CAPTURE,
                    appContext = AppContext(
                        userAction = UserAction.PAY_NOW
                    ),
                    purchaseUnitList = listOf(
                        PurchaseUnit(
                            amount = Amount(
                                currencyCode = CurrencyCode.CAD,
                                value = String.format("%.2f", grandTotal)
                            )
                        )
                    )
                )

                createOrderActions.create(order)
            },
            onApprove = OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.i("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
                    if (captureOrderResult is CaptureOrderResult.Success) {
                        println("Payment Succeeded")
                        //Add MealOrder in DB
                        val order = MealOrder(grandTotal, itemString, restaurantID)
                        bodyForPostingOrder = BodyForPostingOrder(user._id, order)
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
                                        HelperMethods.clearCartCall(user._id, requireContext(),openOrderSuccessPage())
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
                    } else if (captureOrderResult is CaptureOrderResult.Error) {
                        println("Payment Failed")
                        // show error
                    }
                }
            },

            onError = OnError { errorInfo ->
                Log.d("OnError", "Error: $errorInfo")
                println(errorInfo.reason)
            }

        )
    }

    private fun openOrderSuccessPage() {
        HelperMethods.openOrderSuccess(requireContext(), bodyForPostingOrder)
    }

    private fun getCartDetails() {
        mealTotal = 0.0
        taxes = 0.0
        grandTotal = 0.0
        val cartItems = cartItems.items
        val allItemIds = cartItems.map { cartItem -> cartItem.itemId}
        val cartDishesCall = retrofitService.getSpecificDishes(allItemIds)
        cartDishesCall.enqueue(object: Callback<GetSpecificDishesResponse>{
            override fun onResponse(
                call: Call<GetSpecificDishesResponse>,
                response: Response<GetSpecificDishesResponse>
            ) {
                if(response.isSuccessful) {
                    if(response.body() != null) {
                        val dishes = response.body()!!.dishes as MutableList<Dish>
                        println("Before modifying dish size: ${dishes.size}")
                        if(dishes.size != cartItems.size) {
                            for(i in dishes.size until cartItems.size) {
                                val index = dishes.indexOfFirst { dish -> dish._id == cartItems[i].itemId }
                                if(index >= 0) {
                                    dishes.add(dishes[index])
                                }
                            }
                        }
                        println(cartItems.size)
                        println(dishes.size)
                        val cart_adapter = CartAdapter(cartItems as MutableList<CartItem>,
                            dishes, requireContext(), this@CartFragment)
                        cartRecyclerView.apply {
                            adapter = cart_adapter
                            layoutManager = LinearLayoutManager(requireContext())
                        }
                        for (i in dishes.indices) {
                            mealTotal += cartItems[i].quantity.toDouble() * dishes[i].price.toDouble()
                            itemString += cartItems[i].quantity.toString() + " X " + dishes[i].name + "\n"

                        }
                        if(dishes.size > 0) {
                            restaurantID = dishes[0].restaurant_id
                        }
                        else {
                            cart_details_view.visibility = View.INVISIBLE
                        }
                        mealTotal = roundTo2decimal(mealTotal)
                        println(mealTotal)
                        taxes = 0.12*mealTotal
                        taxes = roundTo2decimal(taxes)
                        grandTotal = taxes + mealTotal
                        grandTotal = roundTo2decimal(grandTotal)
                        mealTotalTf.text = "MEAL TOTAL: $" + String.format("%.2f", mealTotal)
                        taxTotaltf.text = "TAXES: $" + String.format("%.2f", taxes)
                        grandTotaltf.text = "GRAND TOTAL: $" + String.format("%.2f", grandTotal)
                        setupPaypal()
                    }

                }
            }

            override fun onFailure(call: Call<GetSpecificDishesResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })

        payviaCardButton.setOnClickListener {
            val card_fragment = CreditCardFragment()
            val order = MealOrder(grandTotal, itemString, restaurantID)
            val bodyForPostingOrder = BodyForPostingOrder(user._id, order)
            val bundle = Bundle()
            bundle.putSerializable(CreditCardFragment.ORDER, bodyForPostingOrder)
            card_fragment.arguments = bundle
            (context as BottomNavigationBarActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, card_fragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDeletion() {
        println("triggered")
        getCartDetails()
    }
}