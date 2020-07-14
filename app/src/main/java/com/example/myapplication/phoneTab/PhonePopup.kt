package com.example.myapplication.phoneTab

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import kotlinx.android.synthetic.main.phone_recycler.*

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

    //확인 버튼 클릭
    fun mOnBookMark(v: View?) {
        //데이터 전달하기
        if(defaultPhone != phone){
            bookmarkPhone(v)
        }

        //액티비티(팝업) 닫기
        finish()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when(event.action){
            MotionEvent.ACTION_UP->{
                val view = findViewById<LinearLayout>(R.id.phoneAddPopup)

                var rect = Rect()
                view.getLocalVisibleRect(rect)

                if(!(rect.left < event.x && event.x < rect.right && rect.top < event.y && event.y < rect.bottom)){
                    finish()
                }
            }
        }


        return true
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
        }
    }

    private fun editPhone(phone: Phone) {
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.LOOKUP_KEY
        )
        var displayName: String? = null

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
            val displayNameColumn = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
            val lookupKeyIndex = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)

            while (cursor.moveToNext()) {
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

    private fun bookmarkPhone(view: View?) {
        val layout = layoutInflater.inflate(R.layout.phone_recycler, null)
        val iv = layout.findViewById<ImageView>(R.id.phoneImageView);
        iv.setImageResource(R.drawable.bookmark_user)
    }
}