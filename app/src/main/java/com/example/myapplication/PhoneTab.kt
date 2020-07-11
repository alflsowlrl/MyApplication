package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PhoneTab(activity: Activity): FragmentTab(){
    private val parentActivity = activity
    var phoneAdapter: PhoneRecycleAdapter = PhoneRecycleAdapter()
    var recycleview: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.phone_tab, container, false)

        recycleview  = view.findViewById<RecyclerView>(R.id.PhoneRecycleView)
        val data:MutableList<Phone> = loadData()

        phoneAdapter.listData = data
        recycleview?.adapter = phoneAdapter
        recycleview?.layoutManager = LinearLayoutManager(activity)

        val floatingButton = view.findViewById<FloatingActionButton>(R.id.phoneAddFloating)
        floatingButton.setOnClickListener {
            val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                // Sets the MIME type to match the Contacts Provider
                type = ContactsContract.RawContacts.CONTENT_TYPE
            }
            parentActivity.startActivity(intent)
        }



        return view
    }

    override fun onResume() {
        super.onResume()

        val data:MutableList<Phone> = loadData()
        phoneAdapter.listData = data
        recycleview?.adapter = phoneAdapter
        recycleview?.layoutManager = LinearLayoutManager(activity)

    }


    private fun loadData(): MutableList<Phone>{
        val data:MutableList<Phone> = mutableListOf()

        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER} = 1"
        val cursor = parentActivity.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")

        val num = 0L

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID)
            val nameTakenColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val id = cursor.getInt(idColumn)
                val name = cursor.getString(nameTakenColumn)
                val TempPhone = cursor.getString(phoneColumn).replace("-", "")
                var phone = PhoneNumberUtils.formatNumber(TempPhone)



                data.add(Phone(id, name, phone))
                Log.d("myApp", "name: $name phone: {$phone} id: $id")
            }
        }

        return data
    }
}