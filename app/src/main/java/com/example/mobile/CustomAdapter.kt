package com.example.mobile

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CustomAdapter(private val viewModel: MyViewModel) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                val item = viewModel.items[adapterPosition]
                val context = view.context
                val currentUserEmail = Firebase.auth.currentUser?.email
                if(currentUserEmail == item.selleremail) {
                    // 사용자가 아이템의 판매자인 경우
                    val intent = Intent(context, WriteActivity::class.java).apply {
                        putExtra("itemid", item.id)
                        putExtra("title", item.title)
                        putExtra("price", item.price.toString())
                        putExtra("body", item.body)
                        putExtra("onsale", item.onSale)
                        putExtra("modify", true)
                }
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, DetailActivity::class.java).apply {
                        putExtra("ITEM_ID", item.id)
                    }
                    context.startActivity(intent)
                }
            }
        }
        fun setContents(pos: Int) {
            val sell = view.findViewById<TextView>(R.id.sell)
            val name = view.findViewById<TextView>(R.id.sellerName)
            var priceOfobj = view.findViewById<TextView>(R.id.price)
            with (viewModel.items[pos]) {
                sell.text = " " + title
                name.text = " 판매자 : "+ sellername + " | " + selleremail
                if(onSale){
                    view.setBackgroundColor(Color.parseColor("#87CEEB"))
                    priceOfobj.text = " 가격 : "+ price + " | (판매중)"
                }
                else{
                    view.setBackgroundColor(Color.parseColor("#FFC0CB"))
                    priceOfobj.text = " 가격 : "+ price + " | (판매종료)"
                }
            }
        }

    }
    // ViewHolder 생성, ViewHolder 는 View 를 담는 상자
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(R.layout.sell_list, viewGroup, false)
        return ViewHolder(view)
    }

    // ViewHolder 에 데이터 연결
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(position)
    }
    override fun getItemCount() = viewModel.items.size
}


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
        fun deleteall(){
            items.clear()
            itemsListData.value = items
        }
    }
