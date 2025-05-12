package com.magangpertamina.apkbukutelepon

import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.magangpertamina.apkbukutelepon.adapter.DataTeleponAdapter
import com.magangpertamina.apkbukutelepon.databinding.ActivityHomeBinding
import com.magangpertamina.apkbukutelepon.modeldata.DataTelepon

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: DataTeleponAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().getReference("DataTelepon")
        adapter = DataTeleponAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Setup SearchView
        setupSearchView()

        fetchDataFromDatabase()

        supportActionBar?.hide()

        binding.imageProfile.setOnClickListener {
            // Buat intent untuk mengarahkan ke LoginAdminActivity
            val intent = Intent(this, LoginAdminActivity::class.java)
            // Jalankan intent
            startActivity(intent)
            // Selesaikan aktivitas saat ini agar pengguna tidak dapat kembali ke HomeActivity
            finish()
        }
    }

    private fun setupSearchView() {
        searchView = binding.searchEditText
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the adapter data
                newText?.let { adapter.filterData(it) }
                return true
            }
        })
    }

    private fun fetchDataFromDatabase() {
        if (!checkInternetConnection()) {
            showToast("Data tidak dapat ditampilkan, periksa koneksi internet Anda")
            return
        }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<DataTelepon>()
                for (dataSnapshot in snapshot.children) {
                    val data = dataSnapshot.getValue(DataTelepon::class.java)
                    data?.let {
                        // Ambil nilai namaKontak dan jabatan dari objek DataTelepon
                        val namaKontak = it.namaKontak
                        val jabatan = it.telpKantor
                        // Tambahkan objek DataTelepon ke dataList
                        dataList.add(it)
                    }
                }
                // Setel data dataList ke adapter
                adapter.setData(dataList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeActivity", "Failed to read value.", error.toException())
            }
        })
    }
    private fun checkInternetConnection(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}