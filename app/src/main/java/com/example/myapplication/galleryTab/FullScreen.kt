package com.example.myapplication.galleryTab

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Gallery
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.phoneTab.Phone
import kotlinx.android.synthetic.main.full_screen.*
import kotlinx.android.synthetic.main.gallery_viewpager.*

class FullScreen: AppCompatActivity() {

    var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.full_screen)
        setContentView(R.layout.gallery_viewpager)

        position = intent.getIntExtra("position", 0)



        // 한 장 받을 때 코드
//        if (intent.hasExtra("img")) {
//            val uri_str = intent.getStringExtra("img")
//            //position = intent.getIntExtra("position", 0)
//            /* "nameKey"라는 이름의 key에 저장된 값이 있다면
//               textView의 내용을 "nameKey" key에서 꺼내온 값으로 바꾼다 */
//            val uri = Uri.parse(uri_str)
//            full.setImageURI(uri)
//        } else {
//            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
//        }

        getAllPhotos()

    }

    private fun getAllPhotos() {
        val fragments = mutableListOf<Fragment>()

        // 모든 사진 정보 가져오기
        val query = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,    // ①
            null,       // ②
            null,       // ③
            null,   // ④
            "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC")    // ⑤

        // Scoped Storage 대응
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                 )
//                if(uri == contentUri){
//                    Log.d("myApp","uri match")
//                }else{
//                    Log.d("myApp","uri not match")
//                }
                // 보여줄 fragment 리스트에 추가
                fragments.add(PhotoFragment.newInstance(contentUri))
            }
        }

        // 어댑터
        val adapter = GalleryPagerAdapter(supportFragmentManager)
        adapter.updateFragments(fragments)
        galleryViewPager.adapter = adapter


        position?.let{galleryViewPager.setCurrentItem(it)}

    }


}