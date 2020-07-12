package com.example.myapplication.memoTab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import java.text.SimpleDateFormat
import java.util.*

class MemoAddActivity : AppCompatActivity() {
    private var editText: EditText? = null
    private var title: String? = null
    private var formattedDate: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_add)
        editText = findViewById(R.id.memoAddTextTitle)
        val btn = findViewById<Button>(R.id.memoAddFinish)
        btn.setOnClickListener {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            formattedDate = df.format(c)
            title = editText?.getText().toString()

            val add = Intent()
            add.putExtra("title", title)
            add.putExtra("time", formattedDate)

            setResult(Activity.RESULT_OK, add)
            finish()
        }
    }
}