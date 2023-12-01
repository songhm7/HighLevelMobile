package com.example.mobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val usersRef = db.collection("users")
    private val selllistRef = db.collection("selllist")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.title = Firebase.auth.currentUser?.email ?: "No User"
        val titleOfShow = findViewById<TextView>(R.id.titleOfShow)
        val msgButton = findViewById<Button>(R.id.messageToSeller)
        var sellerEmail : String = ""
        var price : String = ""
        var onSale = false

        val itemId = intent.getStringExtra("ITEM_ID")
        // Firestore에서 itemId에 해당하는 게시물 정보를 가져와 표시
        if (itemId != null) {
            selllistRef.document(itemId).get().addOnSuccessListener { document ->
                if (document != null) {
                    val title = document.getString("title") ?: "제목 없음"
                    val body = document.getString("body") ?: "내용 없음"
                    price = document.getLong("price")?.toString() ?: "0"
                    sellerEmail = document.getString("selleremail") ?: "이메일 없음"
                    val sellerName = document.getString("sellername") ?: "판매자 없음"
                    onSale = document.getBoolean("onSale") ?: false

                    titleOfShow.text = title
                    //titleOfShow.setTextSize(30f)
                    val isOnSale = findViewById<TextView>(R.id.isOnSale)
                    if(onSale) {
                        isOnSale.text = "판매중"
                        isOnSale.setBackgroundColor(Color.parseColor("#87CEEB")) // Sky Blue 색상
                    } else {
                        isOnSale.text = "판매종료"
                        isOnSale.setBackgroundColor(Color.parseColor("#FFC0CB")) // Pink 색상
                    }
                    findViewById<TextView>(R.id.textViewSeller).text = "판매자 이메일 : $sellerEmail\n판매자명 : $sellerName"
                    findViewById<TextView>(R.id.textViewPrice).text = "가격 : " + price+"원"
                    findViewById<TextView>(R.id.bodyOfShow).text = body
                } else {
                    // 문서가 존재하지 않는 경우 처리
                }
            }.addOnFailureListener {
                // 데이터 조회 실패 처리
            }
        }
        findViewById<Button>(R.id.buttonBack).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        msgButton.setOnClickListener{
            val intent = Intent(this, SendMsgActivity::class.java)
            intent.putExtra("SELLER_EMAIL", sellerEmail) // Intent에 판매자 이메일 추가
            startActivity(intent)
        }
    }

}