package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SendMsgActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sendmsg)
        supportActionBar?.title = Firebase.auth.currentUser?.email ?: "No User"

        findViewById<Button>(R.id.backMsgSend).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val email = findViewById<EditText>(R.id.emailOfReceiver)
        val text = findViewById<EditText>(R.id.textOfMsg)
        val sendButton = findViewById<Button>(R.id.sendButton)

        // Intent에서 판매자 이메일 받기
        val sellerEmail = intent.getStringExtra("SELLER_EMAIL")
        email.setText(sellerEmail) // 이메일 입력 필드에 판매자 이메일 설정

        sendButton.setOnClickListener {
            val receiverEmail = email.text.toString().trim()
            val messageText = text.text.toString().trim()

            if (receiverEmail.isNotEmpty() && messageText.isNotEmpty()) {
                sendMessage(receiverEmail, messageText)
            } else {
                Toast.makeText(this, "상대의 이메일과 쪽지 내용이 비어서는 안됩니다", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun sendMessage(receiverEmail: String, messageText: String) {
        val db = Firebase.firestore
        val messageData = hashMapOf(
            "sender" to Firebase.auth.currentUser?.email,
            "text" to messageText
        )

        db.collection("message")
            .document(receiverEmail)
            .collection("messages")
            .add(messageData)
            .addOnSuccessListener {
                Toast.makeText(this, "메시지 전송 성공", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "메시지 전송 실패", Toast.LENGTH_SHORT).show()
            }
    }
}