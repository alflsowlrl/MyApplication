package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlite.Memo
import com.example.sqlite.SqliteHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import java.util.ArrayList


class MemoTab(activity: Activity): FragmentTab(){
    private val parentActivity = activity
    var memoAdapter = MemoRecycleAdapter(parentActivity)
    var helper = SqliteHelper(parentActivity, "memo", 1)

    companion object{
        const val MEMO_REQUEST_CODE = 99
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.memo_tab, container, false)

        val linearLayoutManager = LinearLayoutManager(parentActivity)


        var recycleview = view.findViewById<RecyclerView>(R.id.memoRecycleView)

        memoAdapter.listData = helper.selectMemo()
        recycleview.adapter = memoAdapter
        recycleview.layoutManager = LinearLayoutManager(activity)
        recycleview.addItemDecoration(DividerItemDecoration(parentActivity, linearLayoutManager.orientation))

        Log.d("myApp", "size: ${memoAdapter.listData.size}")

        val floatingButton = view.findViewById<FloatingActionButton>(R.id.floating)
        floatingButton.setOnClickListener {
            val intent = Intent(parentActivity, MemoAddActivity::class.java)
            parentActivity.startActivityForResult(intent, MEMO_REQUEST_CODE)
        }

        return view
    }

    fun ActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var title = data?.getStringExtra("title")?: ""
            var time = data?.getStringExtra("time")?: ""

            Toast.makeText(parentActivity, "$title,$time", Toast.LENGTH_LONG).show()

            val memo = Memo(null, title, time)
            helper.insertMemo(memo)
            memoAdapter.listData = helper.selectMemo()
        }
    }
}

