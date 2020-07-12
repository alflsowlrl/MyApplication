package com.example.myapplication.phoneTab

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import kotlinx.android.synthetic.main.phone_recycler.view.*

class PhoneRecycleAdapter: RecyclerView.Adapter<PhoneRecycleAdapter.PhoneHolder>() {
    var listData = mutableListOf<Phone>()

    inner class PhoneHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName: TextView

        init {
            tvName = itemView.findViewById(R.id.textTitle)
            itemView.setOnLongClickListener {
                val phone = listData[adapterPosition]

                //데이터 담아서 팝업(액티비티) 호출
                val intent = Intent(itemView.context, PhonePopup::class.java)
                intent.putExtra("id", phone.id)
                intent.putExtra("name", phone.name)
                intent.putExtra("number", phone.phoneNumber)
                startActivity(itemView.context, intent, null)

                false
            }
        }

        fun setPhone(phone: Phone){
            itemView.textTitle.text = phone.name
            itemView.textDate.text = phone.phoneNumber
        }

    }
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

    private fun removeItemView(position: Int) {
        listData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listData.size)
    }
}

