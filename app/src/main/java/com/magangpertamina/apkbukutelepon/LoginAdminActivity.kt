package com.magangpertamina.apkbukutelepon

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.magangpertamina.apkbukutelepon.databinding.ActivityLoginAdminBinding
import com.magangpertamina.apkbukutelepon.modeldata.Admin

class LoginAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginAdminBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup action bar
        supportActionBar?.title = "Login Admin"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enables back button in action bar

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().reference

        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        // Check if admin is already logged in
        if (sharedPref.getBoolean("isAdmin", false)) {
            navigateToAdminHome()
            finish() // Finish activity agar tidak bisa kembali ke halaman login
        }

        // Set up login button
        binding.btnLogin.setOnClickListener {
            val username = binding.loginEmail.text.toString().trim()
            val password = binding.loginPass.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Check user credentials
                checkAdminCredentials(username, password)
            }
        }
    }

    private fun checkAdminCredentials(username: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE  // Tampilkan ProgressBar
        val adminRef = databaseReference.child("Admin")

        adminRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.progressBar.visibility = View.GONE  // Sembunyikan ProgressBar
                    if (dataSnapshot.exists()) {
                        for (adminSnapshot in dataSnapshot.children) {
                            val admin = adminSnapshot.getValue(Admin::class.java)
                            if (admin != null && admin.password == password) {
                                // Correct credentials, show success dialog
                                showSuccessDialog()
                                return
                            }
                        }
                        // Incorrect password
                        Toast.makeText(
                            this@LoginAdminActivity,
                            "Password salah",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Username not found
                        Toast.makeText(
                            this@LoginAdminActivity,
                            "Username tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    binding.progressBar.visibility = View.GONE  // Sembunyikan ProgressBar
                    // Handle database read error
                    Toast.makeText(
                        this@LoginAdminActivity,
                        "Error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Login Berhasil")
            .setMessage("Anda berhasil login sebagai admin")
            .setPositiveButton("Oke") { dialog, which ->
                // Menyimpan status login admin
                val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("isAdmin", true)
                    apply()
                }

                // Redirect to AdminHomeActivity
                val intent = Intent(this, AdminHomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Arahkan ke halaman HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToAdminHome() {
        val intent = Intent(this, AdminHomeActivity::class.java)
        startActivity(intent)
    }

}