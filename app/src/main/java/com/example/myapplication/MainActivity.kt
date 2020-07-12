package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.galleryTab.GalleryTab
import com.example.myapplication.memoTab.MemoTab
import com.example.myapplication.phoneTab.PhoneTab
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_tab_button.view.*

class MainActivity : AppCompatActivity() {
    private lateinit var mContext : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        mContext = applicationContext
        initViewPager() // 뷰페이저와 어댑터 장착

    }

    private fun createView(tabName: String): View {
        var tabView = LayoutInflater.from(mContext).inflate(R.layout.custom_tab_button, null)

        tabView.tab_text.text = tabName
        when (tabName) {
            "메모" -> {
                tabView.tab_logo.setImageResource(android.R.drawable.ic_menu_edit)
                return tabView
            }
            "사진" -> {
                tabView.tab_logo.setImageResource(android.R.drawable.ic_menu_camera)
                return tabView
            }
            "전화" -> {
                tabView.tab_logo.setImageResource(android.R.drawable.ic_menu_call)
                return tabView
            }
            else -> {
                return tabView
            }
        }
    }
    private fun initViewPager(){
        val searchFragment = MemoTab()
        searchFragment.name = "메모 창"

        val cameraFragment = GalleryTab()
        cameraFragment.name = "사진 창"
        val callFragment = PhoneTab()
        callFragment.name = "전화 창"



        val adapter = PageAdapter(supportFragmentManager) // PageAdapter 생성
        adapter.addItems(searchFragment)
        adapter.addItems(cameraFragment)
        adapter.addItems(callFragment)

        main_viewPager.adapter = adapter // 뷰페이저에 adapter 장착
        main_tablayout.setupWithViewPager(main_viewPager) // 탭레이아웃과 뷰페이저를 연동


        main_tablayout.getTabAt(0)?.setCustomView(createView("메모"))
        main_tablayout.getTabAt(1)?.setCustomView(createView("사진"))
        main_tablayout.getTabAt(2)?.setCustomView(createView("전화"))

//        main_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
//            override fun onTabReselected(p0: TabLayout.Tab?) {}
//
//            override fun onTabUnselected(p0: TabLayout.Tab?) {}
//
//            override fun onTabSelected(p0: TabLayout.Tab?) {}
//        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for(fragment in supportFragmentManager.fragments){
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun checkPermission() {
        // 1. 위험권한(Camera) 권한 승인상태 가져오기
        val permissions = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE)
        val premissionInRequest: MutableList<String> = mutableListOf()

        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                premissionInRequest.add(permission)
            }
        }

        if(premissionInRequest.isNotEmpty()){
            requestPermission(premissionInRequest.toTypedArray())
        }

    }

    fun requestPermission(permissions: Array<String>) {
        // 2. 권한 요청
        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            }
        }

        ActivityCompat.requestPermissions( this, permissions, MemoTab.MEMO_REQUEST_CODE)
}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            MemoTab.MEMO_REQUEST_CODE -> {
                for (grantResult in grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        finish()
                    }
                }
            }
        }
    }
}