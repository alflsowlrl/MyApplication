package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.phone_recycler.view.*

class PhoneRecycleAdapter: RecyclerView.Adapter<PhoneHolder>() {
    var listData = mutableListOf<Phone>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.phone_recycler, parent, false)
        return PhoneHolder(view)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: PhoneHolder, position: Int) {
        val phone = listData.get(position)
        holder.setPhone(phone)
    }
}

class PhoneHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    fun setPhone(phone: Phone){
        itemView.textTitle.text = phone.name
        itemView.textDate.text = phone.phoneNumber
    }
}