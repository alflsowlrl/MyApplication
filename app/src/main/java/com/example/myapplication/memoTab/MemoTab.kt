package com.example.myapplication.memoTab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.FragmentTab
import com.example.myapplication.R
import com.example.sqlite.Memo
import com.example.sqlite.SqliteHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MemoTab(): FragmentTab(){
    var memoAdapter: MemoRecycleAdapter? = null
    var memoHelper: SqliteHelper? = null

    companion object{
        const val TAG = "MemoTab"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.context?.let{
            memoAdapter =
                MemoRecycleAdapter(it)
            memoHelper = SqliteHelper(it, MemoConstant.MEMO_DB_NAME, MemoConstant.MEMO_DB_VERSION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =inflater.inflate(R.layout.memo_tab, container, false)
        var recycleview = view.findViewById<RecyclerView>(R.id.memoRecycleView)
        val context = recycleview.context
        val linearLayoutManager = LinearLayoutManager(this.activity)

        val memos = memoHelper?.selectMemo()
        memos?.let{
            memoAdapter?.listData = it
        }

        recycleview.adapter = memoAdapter
        recycleview.layoutManager = LinearLayoutManager(activity)
        recycleview.addItemDecoration(DividerItemDecoration(this.activity, linearLayoutManager.orientation))


        val floatingButton = view.findViewById<FloatingActionButton>(R.id.memoFloating)
        floatingButton.setOnClickListener {
            val intent = Intent(this.activity, MemoAddActivity::class.java)
            intent.putExtra(MemoConstant.MEMO_REQUEST_TYPE_KEY, MemoConstant.MEMO_ADD_REQUEST_TYPE)
            activity?.startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val memos = memoHelper?.selectMemo()
        memos?.let{
            memoAdapter?.listData = it
        }
        memoAdapter?.notifyDataSetChanged()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(requestCode == MemoConstant.MEMO_ADD_REQUEST_CODE && resultCode == Activity.RESULT_OK){
//            ActivityResult(requestCode, resultCode, data)
//        }
//
//    }
//
//
//
//
//    fun ActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK) {
//            var title = data?.getStringExtra("title")?: ""
//            var time = data?.getStringExtra("time")?: ""
//
//            Toast.makeText(this.activity, "$title,$time", Toast.LENGTH_LONG).show()
//
//            val memo = Memo(null, title, time)
//
//
//
//            this.context?.let{
//                memoHelper?.insertMemo(memo)
//
//                val memos = memoHelper?.selectMemo()
//                memos?.let{
//                    memoAdapter?.listData = it
//                }
//            }
//
//        }
//    }
}

