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

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        findViewById<Button>(R.id.sing_up_signup)?.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.email_signup)?.text.toString()
            val password = findViewById<EditText>(R.id.password_signup)?.text.toString()
            val userName = findViewById<EditText>(R.id.username)?.text.toString()
            val birthDay = findViewById<EditText>(R.id.birthDay)?.text.toString()
            if(userEmail.isEmpty()||password.isEmpty()||userName.isEmpty()||birthDay.isEmpty()){
                Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else
                doSignUp(userEmail, password, userName, birthDay)
        }
        findViewById<Button>(R.id.turnBack_signup)?.setOnClickListener{
            startActivity(
                Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun doLogin(userEmail: String, password: String, userName: String, birthDay: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "회원가입에 성공했습니다. 로그인해주세요", Toast.LENGTH_SHORT).show()

                    Firebase.auth.signOut()
                    startActivity(
                        Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun doSignUp(userEmail: String, password: String, userName: String, birthDay: String){
        Firebase.auth.createUserWithEmailAndPassword(userEmail,password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    //데이터베이스에 저장
                    saveUserData(userEmail,password,userName,birthDay)//,Firebase.auth.currentUser?.uid ?: "No User"

                    //로그인 수행
                    doLogin(userEmail,password, userName, birthDay)
                }
                else{
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "회원가입 실패.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("users") // users는 Collection ID

    //새로운 회원의 정보를 데이터베이스에 저장
    private fun saveUserData(userEmail:String, password:String, userName: String, birthDay : String){//, UID : String
        //UID는 굳이 필요 없어보여서 일단 뺐습니다.

        //입력받은 정보들을 FireStore에 저장
        val itemMap = hashMapOf("birth" to birthDay, "email" to userEmail,
            "name" to userName, "password" to password);
        itemsCollectionRef.document(userEmail).set(itemMap) //도큐먼트id를 이메일로 설정
            .addOnSuccessListener {
                // 성공 처리
                println("사용자 데이터 저장 성공")
            }
            .addOnFailureListener {
                // 실패 처리
                println("실패")
            }
    }
}