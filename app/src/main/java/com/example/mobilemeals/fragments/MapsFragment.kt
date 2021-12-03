package com.example.mobilemeals.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mobilemeals.R
import com.example.mobilemeals.helpers.HelperMethods
import com.example.mobilemeals.models.BodyForPostingOrder
import com.example.mobilemeals.models.GetSpecificRestaurantResponse
import com.example.mobilemeals.models.Restaurant
import com.example.mobilemeals.models.UserLoginResponse

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_cart.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.maps.model.Gap

import com.google.android.gms.maps.model.Dash

import com.google.android.gms.maps.model.Dot

import com.google.android.gms.maps.model.PatternItem
import kotlinx.android.synthetic.main.fragment_maps.*
import java.util.*


class MapsFragment : Fragment() {

    lateinit var postingOrderBody: BodyForPostingOrder
    lateinit var user: UserLoginResponse
    lateinit  var mPrefs: SharedPreferences
    val retrofitService = HelperMethods.service
    lateinit var restaurant: Restaurant
    var mapFragment: SupportMapFragment? = null
    companion object {
        val ORDER = "order"
    }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        println("Callback")
        val userLocation = HelperMethods.getLocationFromAddress(requireContext(), user.address + " " + user.city )
        val restaurantLocation = HelperMethods.getLocationFromAddress(requireContext(), restaurant.address + " " + restaurant.city)
        println(userLocation.toString())
        googleMap.addMarker(MarkerOptions().position(userLocation!!).title(user.address))
        googleMap.addMarker(MarkerOptions().position(restaurantLocation!!).title(restaurant.name))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation!!))
        googleMap.setMinZoomPreference(10.0f)

        val polylineOptions = PolylineOptions()
        polylineOptions.add(userLocation)
        polylineOptions.add(restaurantLocation)
        polylineOptions.geodesic(true)
        val pattern: List<PatternItem> = Arrays.asList(
            Dash(20.0f), Gap(20.0f), Dash(20.0f), Gap(20.0f),
            Dash(20.0f), Gap(20.0f), Dash(20.0f), Gap(20.0f),
            Dash(20.0f), Gap(20.0f), Dash(20.0f), Gap(20.0f),
            Dash(20.0f), Gap(20.0f), Dash(20.0f), Gap(20.0f)
        )

        val polyline = googleMap.addPolyline(polylineOptions);
        polyline.pattern = pattern
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if(arguments != null) {
            println("INSIDE MAP FRAGMENT")
             postingOrderBody = requireArguments().getSerializable(ORDER) as BodyForPostingOrder
             mPrefs = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
             val gson = Gson()
             val json = mPrefs.getString("USER", "")
             println(mPrefs.getBoolean("isLoggedIn", false))
             println(json)
             user = gson.fromJson(json, UserLoginResponse::class.java)
            fetchRestaurantDetails()
            order_summary.text = postingOrderBody.order.item_string + "\n" + "$" + postingOrderBody.order.final_price
        }
    }

    private fun fetchRestaurantDetails() {
        val restaurantCall = retrofitService.getSpecificRestaurant(postingOrderBody.order.restaurant_id)
        restaurantCall.enqueue(object: Callback<GetSpecificRestaurantResponse> {
            override fun onResponse(
                call: Call<GetSpecificRestaurantResponse>,
                response: Response<GetSpecificRestaurantResponse>
            ) {
                if(response.isSuccessful)
                {
                    if(response.body() != null) {
                        restaurant = response.body()!!.restaurant
                        mapFragment?.getMapAsync(callback)
                        println("Map Callback should be called now")
                    }
                    else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(requireContext(), jObjError.getString("error"), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<GetSpecificRestaurantResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })
    }
}