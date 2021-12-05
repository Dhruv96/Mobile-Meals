package com.example.mobilemeals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilemeals.R
import com.example.mobilemeals.adapters.AccountAdapter
import kotlinx.android.synthetic.main.fragment_account.*

// This fragment is opened when user selects the account tab in Bottom navigation bar
class AccountFragment : Fragment() {

    val listOptions = listOf<String>("Edit Profile", "Orders", "Logout")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val accAdapter = AccountAdapter(listOptions, requireContext())
        accountRecyclerView.apply {
            adapter = accAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}