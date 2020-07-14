package com.example.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.memoTab.MemoConstant

class SqliteHelper(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val create = "create table memo (" +
                "no integer primary key, " +
                "memoTitle text, " +
                "content text, " +
                "datetime integer" +
                ")"
        db?.execSQL(create)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertMemo(memo:Memo) {
        val values = ContentValues()
        values.put("memoTitle", memo.title)
        values.put("content", memo.content)
        values.put("datetime", memo.datetime)

        val wd = writableDatabase
        wd.insert(MemoConstant.MEMO_DB_NAME, null, values)
        wd.close()
    }

    fun selectMemo(): MutableList<Memo> {
        val list = mutableListOf<Memo>()
        val select = "select * from ${MemoConstant.MEMO_DB_NAME}"
        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while (cursor.moveToNext()) {
            val no = cursor.getLong(cursor.getColumnIndex("no"))
            val title = cursor.getString(cursor.getColumnIndex("memoTitle"))
            val content = cursor.getString(cursor.getColumnIndex("content"))
            val datetime = cursor.getString(cursor.getColumnIndex("datetime"))

            list.add(Memo(no, title, content, datetime))
        }

        cursor.close()
        rd.close()
        return list
    }

    fun updateMemo(memo:Memo) {
        val values = ContentValues()
        values.put("memoTitle", memo.title)
        values.put("content", memo.content)
        values.put("datetime", memo.datetime)

        val wd = writableDatabase
        wd.update(MemoConstant.MEMO_DB_NAME, values, "no = ${memo.no}", null)
        wd.close()
    }

    fun deleteMemo(memo:Memo) {
        val delete = "delete from memo where no = ${memo.no}"
        val db = writableDatabase
        db.execSQL(delete)
        db.close()
    }
}

data class Memo(var no:Long?,var title: String, var content:String, var datetime:String)