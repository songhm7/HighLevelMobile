package com.example.mobile

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {
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

    }
}