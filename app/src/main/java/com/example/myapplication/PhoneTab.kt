package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PhoneTab(activity: Activity): FragmentTab(){
    private val parentActivity = activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.phone_tab, container, false)

        val data:MutableList<Phone> = loadData()
        var phoneAdapter = PhoneRecycleAdapter()

        phoneAdapter.listData = data

        var recycleview = view.findViewById<RecyclerView>(R.id.PhoneRecycleView)
        recycleview.adapter = phoneAdapter
        recycleview.layoutManager = LinearLayoutManager(activity)

        return view
    }

    private fun loadData(): MutableList<Phone>{
        val data:MutableList<Phone> = mutableListOf()

        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER} = 1"
        val cursor = parentActivity.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, null)

        val num = 0L

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID)
            val nameTakenColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val id = cursor.getInt(idColumn)
                val name = cursor.getString(nameTakenColumn)
                val TempPhone = cursor.getString(phoneColumn).replace("-", "")
                var phone = TempPhone

                if(TempPhone.length == 11){
                    phone = TempPhone.subSequence(0, 3).toString() + "-" + TempPhone.subSequence(3, 7).toString() + "-" + TempPhone.subSequence(7, 11).toString()
                }

                data.add(Phone(id, name, phone))
                Log.d("myApp", "name: $name phone: {$phone}")
            }
        }

        return data
    }
}