package com.example.myapplication.phoneTab

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.FragmentTab
import com.example.myapplication.PermissionChecker
import com.example.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PhoneTab(): FragmentTab(){
    var phoneAdapter: PhoneRecycleAdapter =
        PhoneRecycleAdapter()
    var recycleview: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.phone_tab, container, false)

        recycleview  = view.findViewById<RecyclerView>(R.id.PhoneRecycleView)
        val data:MutableList<Phone> = loadDataWithPermissionCheck()

        phoneAdapter.setList(data)
        recycleview?.adapter = phoneAdapter
        recycleview?.layoutManager = LinearLayoutManager(activity)

        val floatingButton = view.findViewById<FloatingActionButton>(R.id.phoneAddFloating)
        floatingButton.setOnClickListener {
            val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                // Sets the MIME type to match the Contacts Provider
                type = ContactsContract.RawContacts.CONTENT_TYPE
            }
            activity?.startActivity(intent)
        }



        return view
    }

    override fun onResume() {
        super.onResume()

        val data:MutableList<Phone> = loadDataWithPermissionCheck()

        Log.d("myPhone", "size: ${data.size}")

        phoneAdapter.setList(data)

        Log.d("myPhone", "size after: ${phoneAdapter.listData.size}")
        recycleview?.adapter = phoneAdapter
        recycleview?.layoutManager = LinearLayoutManager(activity)

    }

    private fun loadDataWithPermissionCheck(): MutableList<Phone>{

        val permissions = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)

        if(PermissionChecker.checkAndRequestPermissons(
                this,
                permissions,
                PermissionChecker.CONTACT_PERMISSION_REQUEST_CODE
            ) == PermissionChecker.ALL_PERMISSION_GRANTED
        ){
            return loadData()
        }
        else{
            return mutableListOf()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PermissionChecker.CONTACT_PERMISSION_REQUEST_CODE -> {
                for (grantResult in grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        activity?.finish()
                    }
                }
            }
        }
    }


    private fun loadData(): MutableList<Phone>{
        val data:MutableList<Phone> = mutableListOf()

        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER} = 1"
        val cursor = activity?.contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID)
            val nameTakenColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val id = cursor.getInt(idColumn)
                val name = cursor.getString(nameTakenColumn)
                val TempPhone = cursor.getString(phoneColumn).replace("-", "")
                var phone = PhoneNumberUtils.formatNumber(TempPhone)



                data.add(
                    Phone(
                        id,
                        name,
                        phone
                    )
                )
            }
        }

        return data
    }
}