package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
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
                    saveUserData(userEmail,password,userName,birthDay,Firebase.auth.currentUser?.uid ?: "No User")
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
                    doLogin(userEmail,password, userName, birthDay)
                }
                else{
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "회원가입 실패.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun saveUserData(userEmail:String, password:String, userName: String, birthDay : String, UID: String){
        //입력받은 이름과 생년월일을 어딘가(스토리지? 베이터베이스?)에 저장하는 코드 작성
    }
}