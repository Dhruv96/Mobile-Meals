package com.example.mobilemeals.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilemeals.BottomNavigationBarActivity
import com.example.mobilemeals.Database.AppDatabase
import com.example.mobilemeals.LoginActivity
import com.example.mobilemeals.R
import com.example.mobilemeals.databinding.AccountRecyclerViewItemBinding
import com.example.mobilemeals.fragments.EditProfileFragment
import com.example.mobilemeals.fragments.OrdersFragment
import kotlinx.android.synthetic.main.account_recycler_view_item.view.*

class AccountAdapter(private val listItems: List<String>, val context: Context) : RecyclerView.Adapter<AccountAdapter.AccountRecyclerViewHolder>() {
    val database = AppDatabase.getInstance(context)
    inner class AccountRecyclerViewHolder (itemView: AccountRecyclerViewItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bindItem(text: String) {
            itemView.listItemTextView.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountRecyclerViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = AccountRecyclerViewItemBinding.inflate(from, parent, false)
        val accountViewHolder = AccountRecyclerViewHolder(binding)
        accountViewHolder.itemView.cardView.setOnClickListener {
            when(accountViewHolder.adapterPosition) {
                0 -> {
                    (context as BottomNavigationBarActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, EditProfileFragment(), "findThisFragment")
                        .addToBackStack(null)
                        .commit()
                }

                1 -> {
                    (context as BottomNavigationBarActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, OrdersFragment(), "findThisFragment")
                        .addToBackStack(null)
                        .commit()
                }

                2 -> {
                    AsyncTask.execute {
                        database?.restaurantDao()?.deleteAll()
                    }
                    val sharedPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
                    sharedPref.edit().clear().apply()
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    (context as Activity).finish()
                }
                else  -> {
                    return@setOnClickListener
                }
            }
        }
        return accountViewHolder
    }

    override fun onBindViewHolder(holder: AccountRecyclerViewHolder, position: Int) {
        holder.bindItem(listItems[position])
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}