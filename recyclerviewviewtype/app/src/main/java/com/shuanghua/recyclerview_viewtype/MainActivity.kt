package com.shuanghua.recyclerview_viewtype

import android.os.Bundle
import androidx.appcompat.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var list = listOf(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "10", "11", "12", "13", "14", "15", "16", "17", "18"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayoutManager = LinearLayoutManager(this)
        val adapter = MyAdapter()
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        adapter.setDada(list, "李四") //默认数据，假设 name 不为空

        button1.setOnClickListener { adapter.setDada(list, "") } // name 为空
        button2.setOnClickListener { adapter.setDada(list, "张三") }
    }
}