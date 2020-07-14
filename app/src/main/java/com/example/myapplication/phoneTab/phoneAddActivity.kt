package com.example.myapplication.phoneTab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import com.example.myapplication.R

class phoneAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_add)

        val editTextName: EditText? = findViewById(R.id.phoneAddName)
        val editTextNumber: EditText? = findViewById(R.id.phoneAddNumber)
        val btn = findViewById<Button>(R.id.phoneAddbtn)


        // Creates a new Intent to insert a contact
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            // Sets the MIME type to match the Contacts Provider
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }

        intent.apply {
            // Inserts an email address
            putExtra(ContactsContract.Intents.Insert.NAME, editTextName?.text)

            // Inserts a phone number
            putExtra(ContactsContract.Intents.Insert.PHONE, editTextNumber?.text)
            /*
             * In this example, sets the phone type to be a work phone.
             * You can set other phone types as necessary.
             */
            putExtra(
                ContactsContract.Intents.Insert.PHONE_TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_WORK
            )
        }

        btn.setOnClickListener {
            startActivity(intent)
        }

    }
}