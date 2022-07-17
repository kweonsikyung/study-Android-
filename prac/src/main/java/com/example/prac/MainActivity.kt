package com.example.prac

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast


class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.btnClick) // 버튼 xml 찾기

        // 버튼 이벤트
        button.setOnClickListener() {
            Toast
                .makeText(this, "toast message", Toast.LENGTH_SHORT)
                .show()
        }
    }
}


