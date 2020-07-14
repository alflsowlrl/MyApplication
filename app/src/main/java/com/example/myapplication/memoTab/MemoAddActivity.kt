package com.example.myapplication.memoTab

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.Image
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

class MemoAddActivity : Activity() {
    private var title: String? = null
    private var formattedDate: String? = null
    val imageList = mutableMapOf<String, Drawable>()

    companion object{
        const val MAX_IMAGE_LIST_SIZE = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_add)
        val editText = findViewById<EditText>(R.id.memoAddTextContent)
        val editTitle = findViewById<EditText>(R.id.memoAddTextTitle)
        val btn = findViewById<Button>(R.id.memoAddFinish)

        val intent = intent
        val requestCode = intent.extras?.getInt(MemoConstant.MEMO_REQUEST_TYPE_KEY)

        var memoId: Long? = null

        when(requestCode){
            MemoConstant.MEMO_ADD_REQUEST_TYPE->{
                Log.d("memoSub", "it is add")
            }
            MemoConstant.MEMO_MOD_REQUEST_TYPE->{
                val content = intent.getStringExtra("content")
                memoId = intent.getLongExtra("id", MemoConstant.DEFAULT_MEMO_ID)
                if(memoId == MemoConstant.DEFAULT_MEMO_ID || memoId == null){
                    Toast.makeText(this, "메모가 존재 하지 않습니다", Toast.LENGTH_LONG).show()
                    finish()
                }
                val memoTitle = intent.getStringExtra("title")

                editText.setText(content.toString())
                editTitle.setText(memoTitle.toString())
                changeTextToImage(editText.text, editText.text.toString(), 0)
            }
            else->{
                Log.d("memoSub", "this is error")
                finish()
            }
        }

        btn.setOnClickListener {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formattedDate = df.format(c)
            val content = editText?.getText().toString()
            val title = editTitle?.text.toString()

            val helper = SqliteHelper(this, MemoConstant.MEMO_DB_NAME, MemoConstant.MEMO_DB_VERSION)

            when(requestCode){
                MemoConstant.MEMO_ADD_REQUEST_TYPE->{
                    Log.d("memoSubAdd", "content: $content")
                    helper.insertMemo(Memo(memoId, title, content, formattedDate))
                }
                MemoConstant.MEMO_MOD_REQUEST_TYPE->{
                    Log.d("memoSubMod", "content: $content")
                    helper.updateMemo(Memo(memoId, title, content, formattedDate))
                }
                else->{
                    Log.d("memoSub", "this is error")
                    finish()
                }
            }
            finish()

        }

        editText?.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                changeTextToImage(editText.text, editText.text.toString(), 0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                val string = p0?.substring(start, start + count)
                Log.d("memoApp", "string: $string")
//                string?.let{changeTextToImage(editText.text, string, start)}
            }
        })
    }

    private fun changeTextToImage(p0: Editable?, text: String, textStart: Int){
        val regex = Regex("""<Image>content://media/external/images/media/(\d+)</Image>""")
        var image: Drawable? = null
        var matchResult = regex.findAll(text)

        Log.d("memoApp", "size: ${matchResult.count()}")

        for(image in imageList){
            val uriWithTag = "<Image>"+image.key+"</Image>"
            if(uriWithTag !in text){
                imageList.remove(image.key)
            }
        }

        var matchCount = 0
        for(match in matchResult){
            val start = match.range.start + textStart
            val end = match.range.last + 1 + textStart
            val uriString = text.subSequence(match.range.start + 7, match.range.last - 7).toString()

            matchCount += 1
            Log.d("memoAdd", "count: ${matchResult.count()}")
            if(matchCount > MAX_IMAGE_LIST_SIZE){
                p0?.delete(start, end)
                Toast.makeText(this, "최대 이미지 개수는 ${MAX_IMAGE_LIST_SIZE}개입니다.", Toast.LENGTH_LONG).show()
                continue
            }


            if((uriString !in imageList.keys)){
                val uri = Uri.parse(uriString)

                val inputStream =  contentResolver.openInputStream(uri)
                image = Drawable.createFromStream(inputStream, uri.toString())
                image.setBounds(0, 0, 300, 300)

                imageList[uriString] = image
            }
            else{
                image = imageList[uriString]
            }

            val span = p0 as Spannable
            image?.let{span.setSpan(ImageSpan(it), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)}

        }
    }
}