package com.example.mobilemeals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.mobilemeals.fragments.AccountFragment
import com.example.mobilemeals.fragments.CartFragment
import com.example.mobilemeals.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_bottom_navigation_bar.*


class BottomNavigationBarActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val cartFragment = CartFragment()
    private val accountFragment = AccountFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation_bar)
        replaceFragment(homeFragment)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.cart -> replaceFragment(cartFragment)
                R.id.account -> replaceFragment(accountFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}