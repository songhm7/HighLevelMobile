package com.example.mobile

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MyViewModel>()
    private var spinnerSelect = 1 // Spinner 선택을 추적하는 변수

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_sign_out -> {
                // 로그아웃 로직
                Firebase.auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.menu_write_new_post -> {
                startActivity(Intent(this, WriteActivity::class.java))
                finish()
                true
            }
            R.id.menu_write_message -> {
                startActivity(Intent(this, SendMsgActivity::class.java))
                finish()
                true
            }
            R.id.menu_view_message -> {
                startActivity(Intent(this, ViewMsgActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Firebase.auth.currentUser == null) { //로그인 안돼있으면 로그인액티비티로 전환
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        supportActionBar?.title = Firebase.auth.currentUser?.email ?: "No User"

        //판매글 목록
        val adapter = CustomAdapter(viewModel)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter // RecyclerView와 CustomAdapter 연결
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        
        //구분선
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        viewModel.itemsListData.observe(this) { // 데이터에 변화가 있을 때 어댑터에게 변경을 알림
            adapter.notifyDataSetChanged() // 어댑터가 리사이클러뷰에게 알려 내용을 갱신함
        }

        val db: FirebaseFirestore = Firebase.firestore
        val itemsCollectionRef = db.collection("selllist") // users는 Collection ID

        //selllist의 모든 문서를 리사이클러뷰에 표시합니다.
        itemsCollectionRef.get().addOnSuccessListener { documents ->
            for(document in documents){
                val item = Item(
                    id = document.id,
                    title = document.getString("title") ?: "제목없음",
                    body = document.getString("body") ?: "내용없음",
                    onSale = document.getBoolean("onSale") ?: false,
                    price = document.getLong("price")?.toInt() ?: 0,
                    selleremail = document.getString("selleremail") ?: "이메일없음",
                    sellername = document.getString("sellername") ?: "판매자없음"
                )
                viewModel.addItem(item)
            }
        }

        //판매여부 필터링에 사용할 스피너(콤보박스)
        val spinnerSaleStatus = findViewById<Spinner>(R.id.onSaleCheck)
        ArrayAdapter.createFromResource(
            this,
            R.array.sale_status_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSaleStatus.adapter = adapter
        }
        spinnerSaleStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (parent.getItemAtPosition(position).toString()) {
                    "전체" -> { spinnerSelect = 1 }
                    "판매중" -> { spinnerSelect = 2 }
                    "판매종료" -> { spinnerSelect = 3 }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                //spinnerSelect = 1
            }
        }
        spinnerSaleStatus.setSelection(0) // 스피너 기본값으로 인덱스 0으로 설정

        //필터 적용
        findViewById<Button>(R.id.button_filter).setOnClickListener {
            //일단 현재 리스트를 전부 지운 뒤
            viewModel.deleteall()

            val query_id = findViewById<EditText>(R.id.query_id)
            val query_mail = findViewById<EditText>(R.id.query_mail)
            val query_title = findViewById<EditText>(R.id.query_title)
            val query_max = findViewById<EditText>(R.id.query_max)
            val query_min = findViewById<EditText>(R.id.query_min)

            //필터 적용
            val query_result = when (spinnerSelect) {
                2 -> itemsCollectionRef.whereEqualTo("onSale", true)
                3 -> itemsCollectionRef.whereEqualTo("onSale", false)
                else -> itemsCollectionRef
            }
            query_result.get().addOnSuccessListener { documents ->
                var items = ArrayList<Item>()
                for(document in documents){
                    val item = Item(
                        id = document.id,
                        title = document.getString("title") ?: "제목없음",
                        body = document.getString("body") ?: "내용없음",
                        onSale = document.getBoolean("onSale") ?: false,
                        price = document.getLong("price")?.toInt() ?: 0,
                        selleremail = document.getString("selleremail") ?: "이메일없음",
                        sellername = document.getString("sellername") ?: "판매자없음"
                    )
                    items.add(item)
                }

                if (!query_id.getText().toString().isEmpty()){
                    val id = query_id.getText().toString()
                    items = ArrayList(items.filter { it.sellername.contains(id) })
                }
                if (!query_title.getText().toString().isEmpty()){
                    val title = query_title.getText().toString()
                    items = ArrayList(items.filter { it.title.contains(title) })
                }

                if (!query_max.getText().toString().isEmpty()){
                    val maxPrice = Integer.parseInt(query_max.getText().toString())
                    items = ArrayList(items.filter { it.price <= maxPrice })
                }

                if (!query_min.getText().toString().isEmpty()){
                    val minPrice = Integer.parseInt(query_min.getText().toString())
                    items = ArrayList(items.filter { it.price >= minPrice })
                }
                if (!query_mail.getText().toString().isEmpty()){
                    val email = query_mail.getText().toString()
                    items = ArrayList(items.filter { it.selleremail.contains(email) })
                }

                for (item in items){
                    viewModel.addItem(item)
                }

            }
        }
    }
}