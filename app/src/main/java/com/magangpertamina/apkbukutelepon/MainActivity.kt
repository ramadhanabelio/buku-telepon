package com.magangpertamina.apkbukutelepon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.magangpertamina.apkbukutelepon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // arahkan kehalaman HomeActivity.kt
        binding.btnGetStarted.setOnClickListener {
            // Buat intent untuk mengarahkan ke HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            // Jalankan intent
            startActivity(intent)
            // Selesaikan aktivitas saat ini agar pengguna tidak dapat kembali ke MainActivity
            finish()
        }

    }
}