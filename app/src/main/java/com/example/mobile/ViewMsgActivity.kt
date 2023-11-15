package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ViewMsgActivity : AppCompatActivity() {
    private val messages = mutableListOf<Message>() // 메시지 리스트 선언
    private lateinit var messagesAdapter: MessageAdapter // 어댑터 선언
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewmsg)
        supportActionBar?.title = Firebase.auth.currentUser?.email ?: "No User"

        val db = Firebase.firestore
        val currentUserEmail = Firebase.auth.currentUser?.email ?: return

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 어댑터 초기화
        messagesAdapter = MessageAdapter(messages)
        recyclerView.adapter = messagesAdapter

        val messagesRef = db.collection("message").document(currentUserEmail).collection("messages")
        messagesRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val sender = document.getString("sender") ?: "알 수 없음"
                val text = document.getString("text") ?: ""
                val message = Message(sender, text)
                messages.add(message) // 메시지 리스트에 추가
            }
            messagesAdapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
        }

        findViewById<Button>(R.id.backMsgView).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}