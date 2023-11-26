package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
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
        supportActionBar?.title = Firebase.auth.currentUser?.email ?: "No User"
        val userEmail = Firebase.auth.currentUser?.email.toString()

        //글을 수정하는 경우
        val title = intent.getStringExtra("title")
        val price = intent.getStringExtra("price")
        val body = intent.getStringExtra("body")
        val onSaleIntent = intent.getBooleanExtra("onsale",false)
        val isModify = intent.getBooleanExtra("modify", false)
        //내용 채우기
        if (isModify) {
            findViewById<EditText>(R.id.titleOfPost).setText(title)
            findViewById<EditText>(R.id.priceOfPost).setText(price.toString())
            findViewById<EditText>(R.id.bodyOfPost).setText(body)
            findViewById<CheckBox>(R.id.ifOnSale).isChecked = onSaleIntent
        }

        //사용자 정보 불러오기
        usersRef.document(userEmail).get().addOnSuccessListener { document ->
            if(document != null){
                userName = document.getString("name") ?: "NoName"
            }
        }

        findViewById<Button>(R.id.back).setOnClickListener { //뒤로가기 버튼
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.post).setOnClickListener { //게시하기 버튼
            val title = findViewById<EditText>(R.id.titleOfPost).text.toString()
            val pricestr = findViewById<EditText>(R.id.priceOfPost).text.toString()
            val body = findViewById<EditText>(R.id.bodyOfPost).text.toString()
            val onSale = findViewById<CheckBox>(R.id.ifOnSale).isChecked
            if(!title.equals("") && !pricestr.equals("") && !body.equals("")) {
                val priceint: Int = pricestr.toInt()
                post(userEmail, userName, title, priceint, body, onSale, isModify)
                Toast.makeText(this, "게시완료.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this, "제목과 본문, 가격을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

        }

        
    }

    private fun post(userEmail : String, userName: String, title : String, price : Int, body : String, onSale : Boolean, modify : Boolean){
        val postMap = hashMapOf(
            "onSale" to onSale,
            "price" to price,
            "selleremail" to userEmail,
            "sellername" to userName,
            "title" to title,
            "body" to body
        )
        //수정하는 거면
        if(modify){
            //해당 글의 아이디를 받은 뒤,
            val itemId = intent.getStringExtra("itemid").toString()
            
            //그 아이디의 데이터를 postMap의 내용으로 대체
            selllistRef.document(itemId).set(postMap)
                .addOnSuccessListener { Log.d("WriteActivity","Post modified successfully")  }
                .addOnFailureListener{ e-> Log.w("WriteActivity","Error modifying post",e)}
        }
        else{
            selllistRef.add(postMap)
                .addOnSuccessListener { Log.d("WriteActivity","Post added successfully")  }
                .addOnFailureListener{ e-> Log.w("WriteActivity","Error adding post",e)}
        }
        
    }

}