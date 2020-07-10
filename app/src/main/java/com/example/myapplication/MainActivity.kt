package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlite.SqliteHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_tab_button.view.*
import kotlinx.android.synthetic.main.phone_tab.*

class MainActivity : AppCompatActivity() {
    private lateinit var mContext : Context
    private var memoTab: MemoTab = MemoTab(this)
    var helper = SqliteHelper(this, "memo", 1)

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
            "찾기" -> {
                tabView.tab_logo.setImageResource(android.R.drawable.ic_menu_search)
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
        memoTab = MemoTab(this)
        val searchFragment = memoTab
        searchFragment.name = "찾기 창"

        val cameraFragment = FragmentTab()
        cameraFragment.name = "사진 창"
        val callFragment = PhoneTab(this)
        callFragment.name = "전화 창"



        val adapter = PageAdapter(supportFragmentManager) // PageAdapter 생성
        adapter.addItems(searchFragment)
        adapter.addItems(cameraFragment)
        adapter.addItems(callFragment)

        main_viewPager.adapter = adapter // 뷰페이저에 adapter 장착
        main_tablayout.setupWithViewPager(main_viewPager) // 탭레이아웃과 뷰페이저를 연동


        main_tablayout.getTabAt(0)?.setCustomView(createView("찾기"))
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

        if(requestCode == MemoTab.MEMO_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            memoTab?.ActivityResult(requestCode, resultCode, data)
       }
    }

    fun checkPermission() {
        // 1. 위험권한(Camera) 권한 승인상태 가져오기
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        }
    }

    fun requestPermission() {
        // 2. 권한 요청
        ActivityCompat.requestPermissions( this, arrayOf(Manifest.permission.READ_CONTACTS), MemoTab.MEMO_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            99 -> {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                }
            }
        }
    }




}