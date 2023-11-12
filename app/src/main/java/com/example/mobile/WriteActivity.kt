package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WriteActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val usersRef = db.collection("users")
    private val selllistRef = db.collection("selllist")
    private var userName: String = "NoName"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        val userEmail = Firebase.auth.currentUser?.email.toString()

        //사용자 정보 불러오기
        usersRef.document(userEmail).get().addOnSuccessListener { document ->
            if(document != null){
                userName = document.getString("name") ?: "NoName"
            }
        }

        findViewById<Button>(R.id.back).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<Button>(R.id.post).setOnClickListener {
            try {
                val title = findViewById<EditText>(R.id.titleOfPost).text.toString()
                val pricestr = findViewById<EditText>(R.id.priceOfPost).text.toString()
                val priceint: Int = pricestr.toInt()
                val body = findViewById<EditText>(R.id.bodyOfPost).text.toString()
                post(userEmail, userName, title, priceint, body)
                Toast.makeText(this, "게시완료.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } catch(e : NumberFormatException){
                Toast.makeText(this, "가격을 설정해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun post(userEmail : String, userName: String, title : String, price : Int, body : String){
        val postMap = hashMapOf(
            "onSale" to true,
            "price" to price,
            "selleremail" to userEmail,
            "sellername" to userName,
            "title" to title,
            "body" to body
        )
        selllistRef.add(postMap)
            .addOnSuccessListener { Log.d("WriteActivity","Post added successfully")  }
            .addOnFailureListener{ e-> Log.w("WriteActivity","Error adding post",e)}
    }

}