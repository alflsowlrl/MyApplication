package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.myapplication.R

class PhonePopup : Activity() {
    var txtText: TextView? = null
    var phone: Phone? = null
    val defaultPhone = Phone(-100, "", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_phone_popup)

        //UI 객체생성
        txtText = findViewById<View>(R.id.txtText) as TextView

        //데이터 가져오기
        val intent = getIntent()

        val id = intent.getIntExtra("id", -100)
        val name = intent.getStringExtra("name") ?: ""
        val number = intent.getStringExtra("number") ?: ""
        txtText!!.text = "$name: $number"

        phone = Phone(id, name, number)
    }

    //확인 버튼 클릭
    fun mOnMod(v: View?) {
        //데이터 전달하기
        if(defaultPhone != phone){
            editPhone(phone!!)
        }

        //액티비티(팝업) 닫기
        finish()
    }

    fun mOnDel(v: View?) {
        if(defaultPhone != phone){
            removePhone(phone!!)
        }
        //액티비티(팝업) 닫기
        finish()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //바깥레이어 클릭시 안닫히게
        return if (event.action == MotionEvent.ACTION_OUTSIDE) {
            false
        } else true
    }

    override fun onBackPressed() {
        //안드로이드 백버튼 막기
        return
    }

    private fun removePhone(phone: Phone) {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )
        var displayName: String? = null
        var contentUri: Uri? = null
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                displayName = cursor.getString(displayNameColumn)
                if (displayName != phone.name) {
                    continue
                }
                contentUri = Uri.withAppendedPath(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    id.toString()
                )
            }
        }

        // 찾은 Uri를 MediaStore에서 삭제
        contentUri?.let{
            contentResolver.delete(contentUri!!, null, null)
            Log.d("myApp", "Removed $displayName from MediaStore: $contentUri")
        }
    }

    private fun editPhone(phone: Phone) {
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.LOOKUP_KEY
        )
        var displayName: String? = null
        var contentUri: Uri? = null
        var id: Long? = null

        // The index of the contact's _ID value
        var idIndex: Int = 0
        // The lookup key from the Cursor
        var currentLookupKey: String? = null
        // The _ID value from the Cursor
        var currentId: Long = 0
        // A content URI pointing to the contact
        var selectedContactUri: Uri? = null


        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
            val lookupKeyIndex = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)

            while (cursor.moveToNext()) {
                id = cursor.getLong(idColumn)
                displayName = cursor.getString(displayNameColumn)
                currentLookupKey = cursor.getString(lookupKeyIndex)
                if (displayName != phone.name) {
                    continue
                }
                else{
                    selectedContactUri = ContactsContract.Contacts.getLookupUri(currentId, currentLookupKey)
                }
            }
        }

        val editIntent = Intent(Intent.ACTION_EDIT).apply {
            /*
             * Sets the contact URI to edit, and the data type that the
             * Intent must match
             */
            setDataAndType(selectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
        }

        editIntent.putExtra("finishActivityOnSaveCompleted", true)
        ContextCompat.startActivity(this, editIntent, null)
    }
}