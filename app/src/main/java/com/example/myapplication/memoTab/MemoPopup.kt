package com.example.myapplication.memoTab

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.phoneTab.Phone
import com.example.sqlite.Memo
import com.example.sqlite.SqliteHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MemoPopup : Activity() {

    val helper = SqliteHelper(this, "memo", 1)
    var memo: Memo? = null
    companion object{
        const val TAG = "MemoTab"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_memo_popup)

        val view = findViewById<TextView>(R.id.memoText)
        val title = intent.getStringExtra("title")?: ""
        val id = intent.getLongExtra("id", MemoConstant.DEFAULT_MEMO_ID)
        view.text = "$title\n삭제하시겠습니까?"
        memo = Memo(id, "", "", "")
    }



    override fun onTouchEvent(event: MotionEvent): Boolean {

        when(event.action){
            MotionEvent.ACTION_UP->{
                val view = findViewById<LinearLayout>(R.id.memoAddPopup)

                var rect = Rect()
                view.getLocalVisibleRect(rect)

                if(!(rect.left < event.x && event.x < rect.right && rect.top < event.y && event.y < rect.bottom)){
                    finish()
                }
            }
        }
        return true
    }

    // 데이터 삭제
    private fun removeMemo(memo: Memo) {
        helper.deleteMemo(memo)
    }

    fun memoDel(v: View?) {
        if(memo?.no != MemoConstant.DEFAULT_MEMO_ID){
            removeMemo(memo!!)
        }
        //액티비티(팝업) 닫기
        finish()
    }

    fun memoCancel(v: View?){
        finish()
    }

    fun memoMod(v: View?) {
        if(memo?.no != MemoConstant.DEFAULT_MEMO_ID){
            val intent = Intent(this, MemoModActivity::class.java)
            intent.putExtra("content", memo?.content)
            intent.putExtra("id", memo?.no)
            this.startActivity(intent)
        }

        //액티비티(팝업) 닫기
        finish()
    }

    fun memoSearch(v: View?) {

        if(memo?.no != MemoConstant.DEFAULT_MEMO_ID){
            val intent = Intent(Intent.ACTION_WEB_SEARCH)
            intent.putExtra(SearchManager.QUERY, memo?.content)

            if(intent.resolveActivity(this.packageManager) != null){
                this.startActivity(intent)
            }
            else{
                val msg = "Sorry, there is no web browser available"
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        }

        //액티비티(팝업) 닫기
        finish()
    }
}