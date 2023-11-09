package com.example.mobile

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Firebase.auth.currentUser == null) { //로그인 안돼있으면 로그인액티비티로 전환
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.textUID).text = Firebase.auth.currentUser?.uid ?: "No User" //현재유저id표시
        findViewById<Button>(R.id.button_signout).setOnClickListener {//로그아웃버튼
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


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
        itemsCollectionRef.get().addOnSuccessListener {
            for (doc in it)
                viewModel.addItem(Item(doc["title"].toString(), doc["sellername"].toString(),
                    doc["price"].toString())) //price는 firestore에서 number로 받는데 일단 string으로 해놨습니다.
        }
    }
}