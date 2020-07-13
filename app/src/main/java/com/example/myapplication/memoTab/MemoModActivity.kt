package com.example.myapplication.memoTab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.sqlite.Memo
import com.example.sqlite.SqliteHelper
import java.text.SimpleDateFormat
import java.util.*

class MemoModActivity : AppCompatActivity() {
    private var editText: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_mod)
        editText = findViewById(R.id.memoModTextTitle)

        val content = intent.getStringExtra("content")
        val id = intent.getLongExtra("id", MemoConstant.DEFAULT_MEMO_ID)
        editText?.setText(content.toString())

        if(id == MemoConstant.DEFAULT_MEMO_ID){
            Toast.makeText(this, "메모가 존재 하지 않습니다", Toast.LENGTH_LONG).show()
            finish()
        }

        val btn = findViewById<Button>(R.id.memoModFinish)
        btn.setOnClickListener {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formattedDate = df.format(c) ?: ""
            val title = editText?.getText().toString() ?: ""


            val helper = SqliteHelper(this, MemoConstant.MEMO_DB_NAME, MemoConstant.MEMO_DB_VERSION)
            helper.updateMemo(Memo(id, title, formattedDate))

            val add = Intent()
            add.putExtra("title", title)
            add.putExtra("time", formattedDate)

            setResult(Activity.RESULT_OK, add)
            finish()
        }
    }
}