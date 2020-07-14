package com.example.myapplication.phoneTab

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.example.myapplication.R
import kotlinx.android.synthetic.main.phone_recycler.view.*

class PhoneRecycleAdapter: RecyclerView.Adapter<PhoneRecycleAdapter.PhoneHolder>() {
    var listData = mutableListOf<Pair<Phone, Int>>()

    companion object{
        const val IMAGE_VIEW_KEY = 1
    }

    inner class PhoneHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName: TextView

        init {
            tvName = itemView.findViewById(R.id.textTitle)
            val imageView = itemView.findViewById<ImageView>(R.id.phoneImageView)
            imageView.setTag(R.id.phoneImageView, "user")
            val nameNumberHolder = itemView.findViewById<LinearLayout>(R.id.nameNumberHolder)
            nameNumberHolder.setOnLongClickListener {
                val phone = listData[adapterPosition].first

                //데이터 담아서 팝업(액티비티) 호출
                val intent = Intent(itemView.context, PhonePopup::class.java)
                intent.putExtra("id", phone.id)
                intent.putExtra("name", phone.name)
                intent.putExtra("number", phone.phoneNumber)
                startActivity(itemView.context, intent, null)

                false
            }

            imageView.setOnClickListener {
                val id = listData[adapterPosition].first.id
                when(imageView.getTag(R.id.phoneImageView)){
                    "user"->{
                        imageView.setImageResource(R.drawable.user_bookmark)
                        imageView.setTag(R.id.phoneImageView, "user_bookmark")
                        changePriority(id, PhonePriority.HIGH_PRIORITY)
                    }
                    else->{
                        imageView.setImageResource(R.drawable.user)
                        imageView.setTag(R.id.phoneImageView, "user")
                        changePriority(id, PhonePriority.LOW_PRIORITY)
                    }
                }
            }


        }

        fun setPhone(phone: Pair<Phone, Int>){
            itemView.textTitle.text = phone.first.name
            itemView.textDate.text = phone.first.phoneNumber
            val imageView = itemView.findViewById<ImageView>(R.id.phoneImageView)
            when(phone.second){
                PhonePriority.HIGH_PRIORITY->{
                    imageView.setImageResource(R.drawable.user_bookmark)
                    imageView.setTag(R.id.phoneImageView, "user_bookmark")
                }
                else->{
                    imageView.setImageResource(R.drawable.user)
                    imageView.setTag(R.id.phoneImageView, "user")
                }
            }
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

    fun checkIsinList(phone: Phone): Boolean{
        for(elem in listData){
            if(elem.first.id == phone.id){
                return true
            }
        }
        return false
    }

    fun isPhoneinPhoneBookWithMod(id: Int, phoneBook: MutableList<Phone>): Boolean{
        val itr = phoneBook.iterator()
        while(itr.hasNext()){
            val elem = itr.next()
            if(id == elem.id){

                return true
            }
        }

        return false
    }

    fun setList(list: MutableList<Phone>){
        for(phone in list){
            val isInList = checkIsinList(phone)
            if(!isInList){
                listData.add(Pair(phone, PhonePriority.LOW_PRIORITY))
            }
        }

        val itr = listData.iterator()
        while(itr.hasNext()){
            val phone = itr.next()
            var isInphoneBook = false

            val itrIn = list.iterator()
            while(itrIn.hasNext()){
                val elem = itrIn.next()
                if(phone.first.id == elem.id){
                    phone.first.name = elem.name
                    phone.first.phoneNumber = elem.phoneNumber
                    isInphoneBook = true
                }
            }

            if(!isInphoneBook) {
                itr.remove()
            }
        }

        listData.sortWith(compareBy({it.second}, {it.first.name}))
        this.notifyDataSetChanged()
    }

    fun changePriority(id: Int, priority: Int){
        for(i in 0 until listData.size){
            if(id == listData[i].first.id){

                listData[i] = Pair(listData[i].first, priority)
                break
            }
        }

        listData.sortWith(compareBy({it.second}, {it.first.name}))

        this.notifyDataSetChanged()
    }
}

