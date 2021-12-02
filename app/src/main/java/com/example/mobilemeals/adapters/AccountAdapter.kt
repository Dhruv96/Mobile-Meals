package com.example.mobilemeals.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilemeals.BottomNavigationBarActivity
import com.example.mobilemeals.R
import com.example.mobilemeals.databinding.AccountRecyclerViewItemBinding
import com.example.mobilemeals.fragments.EditProfileFragment
import kotlinx.android.synthetic.main.account_recycler_view_item.view.*

class AccountAdapter(private val listItems: List<String>, val context: Context) : RecyclerView.Adapter<AccountAdapter.AccountRecyclerViewHolder>() {

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
                    println("Orders")
                }

                2 -> {
                   println("Logout")
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