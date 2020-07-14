package com.example.myapplication.memoTab

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.util.Log
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
    val imageList = mutableMapOf<String, Drawable>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_mod)
        val editText = findViewById<EditText>(R.id.memoModTextTitle)

        val content = intent.getStringExtra("content")
        val id = intent.getLongExtra("id", MemoConstant.DEFAULT_MEMO_ID)
        editText.setText(content.toString())

        changeTextToImage(editText.text)

        if(id == MemoConstant.DEFAULT_MEMO_ID){
            Toast.makeText(this, "메모가 존재 하지 않습니다", Toast.LENGTH_LONG).show()
            finish()

        }

        val btn = findViewById<Button>(R.id.memoModFinish)
        btn.setOnClickListener {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formattedDate = df.format(c) ?: ""
            val title = editText?.getText().toString()


            val helper = SqliteHelper(this, MemoConstant.MEMO_DB_NAME, MemoConstant.MEMO_DB_VERSION)
            helper.updateMemo(Memo(id, "", title, formattedDate))

            finish()
        }

        editText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                p0?.let{changeTextToImage(p0)}
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    fun changeTextToImage(p0: Editable?){
        val regex = Regex("""<Image>content://media/external/images/media/(\d+)</Image>""")
        var image: Drawable? = null
        var matchResult = regex.findAll(p0.toString())

        Log.d("memoApp", "size: ${matchResult.count()}")

        for(image in imageList){
            val uriWithTag = "<Image>"+image.key+"</Image>"
            if(uriWithTag !in p0.toString()){
                imageList.remove(image.key)
            }
        }

        for(match in matchResult){
            val start = match.range.start
            val end = match.range.last + 1
            val uriString = p0.toString().subSequence(match.range.start + 7, match.range.last - 7).toString()
            Log.d("memoApp", "start: ${start} last: ${end} content: ${uriString}")

            if((uriString !in imageList.keys)){
                val uri = Uri.parse(uriString)

                val inputStream =  contentResolver.openInputStream(uri)
                image = Drawable.createFromStream(inputStream, uri.toString())
                image.setBounds(0, 0, 300, 300)

                if(image != null){
                    imageList[uriString] = image
                }
            }
            else{
                image = imageList[uriString]
            }

            Log.d("memoApp", "${image.toString()}")

            val span = p0 as Spannable
            image?.let{span.setSpan(ImageSpan(it), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)}

        }
    }
}