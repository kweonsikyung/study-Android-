package com.example.showtext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnToast : Button = findViewById(R.id.startButton)

        btnToast.setOnClickListener{
            Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show()
        }
    }
}
