package com.example.myapplication

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlite.Memo
import com.example.sqlite.SqliteHelper
import io.realm.Realm
import kotlinx.android.synthetic.main.memo_item.view.*
import kotlinx.android.synthetic.main.phone_recycler.view.*

/**
 * Created by ysh on 2018-04-12.
 */


class MemoRecycleAdapter(activity: Activity): RecyclerView.Adapter<MemoRecycleAdapter.MemoHolder>() {
    var listData = mutableListOf<Memo>()
    var helper = SqliteHelper(activity, "memo", 1)
    inner class MemoHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName: TextView

        init {
            tvName = itemView.findViewById(R.id.mContextTextView)
            itemView.setOnLongClickListener {
                removeMemo(listData[adapterPosition])
                removeItemView(adapterPosition)
                false
            }
        }
        fun setMemo(memo: Memo){
            itemView.mContextTextView.text = memo.content
            itemView.mContextTextViewTime.text = memo.datetime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memo_item, parent, false)
        return MemoHolder(view)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: com.example.myapplication.MemoRecycleAdapter.MemoHolder, position: Int) {
        val memo = listData.get(position)
        holder.setMemo(memo)
    }

    fun addElment(memo: Memo){
        listData.add(memo)
    }

    private fun removeItemView(position: Int) {
        listData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listData.size)
    }

    // 데이터 삭제
    private fun removeMemo(memo: Memo) {
        helper.deleteMemo(memo)
    }
}

