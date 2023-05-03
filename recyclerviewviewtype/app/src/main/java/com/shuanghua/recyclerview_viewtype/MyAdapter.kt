package com.shuanghua.recyclerview_viewtype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var name: String = ""
    private var list: List<String> = ArrayList()

    fun setDada(list: List<String>, name: String) {
        //从外部把数据传给 list 和 name
        this.list = list
        this.name = name
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            ViewHolder1(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
            )
        } else {
            ViewHolder2(
                LayoutInflater.from(parent.context).inflate(R.layout.item_footer, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) {
            TYPE_FOOTER
        } else {
            TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return if (name == "") {
            list.size
        } else {
            list.size + 1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder1 && position < list!!.size) {
            holder.bind(list!![position])
        } else if (holder is ViewHolder2 && position == list!!.size) {
            holder.bind(name)
        }
    }

    class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView = itemView.findViewById<TextView>(android.R.id.text1)
        fun bind(data: String) {
            textView.text = data
        }
    }

    class ViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView = itemView.findViewById<TextView>(R.id.footer)

        fun bind(name: String) {
            textView.text = name
        }
    }

    companion object {
        const val TYPE_ITEM = 0
        const val TYPE_FOOTER = 1
    }
}