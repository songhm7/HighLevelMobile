package com.example.mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val viewModel: MyViewModel) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun setContents(pos: Int) {
            val sell = view.findViewById<TextView>(R.id.sell)
            val sellername = view.findViewById<TextView>(R.id.sellerName)
            val price = view.findViewById<TextView>(R.id.price)
            with (viewModel.items[pos]) {
                sell.text = title
                sellername.text = "  판매자 : "+name
                price.text = "  가격 : "+itemPrice
            }
        }

    }
    // ViewHolder 생성, ViewHolder 는 View 를 담는 상자
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(R.layout.sell_list, viewGroup, false)
        val viewHolder = ViewHolder(view)
        view.setOnClickListener {
            viewModel.itemClickEvent.value = viewHolder.adapterPosition
        }
        view.setOnLongClickListener {
            viewModel.itemLongClick = viewHolder.adapterPosition
            false // for context menu
        }
        return viewHolder
    }
    // ViewHolder 에 데이터 연결
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(position)
    }
    override fun getItemCount() = viewModel.items.size
}


    data class Item(val title: String, val name: String, val itemPrice : String)
    class MyViewModel : ViewModel() {
        val itemsListData = MutableLiveData<ArrayList<Item>>()
        val items = ArrayList<Item>()
        val itemClickEvent = MutableLiveData<Int>()
        var itemLongClick = -1
        fun addItem(item: Item) {
            items.add(item)
            itemsListData.value = items // let the observer know the livedata changed
        }
        fun updateItem(pos: Int, item: Item) {
            items[pos] = item
            itemsListData.value = items // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
        }
        fun deleteItem(pos: Int) {
            items.removeAt(pos)
            itemsListData.value = items
        }
    }
