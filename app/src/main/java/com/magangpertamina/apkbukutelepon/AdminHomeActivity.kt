package com.magangpertamina.apkbukutelepon

import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.magangpertamina.apkbukutelepon.adapter.DataTeleponAdapter
import com.magangpertamina.apkbukutelepon.databinding.ActivityAdminHomeBinding
import com.magangpertamina.apkbukutelepon.modeldata.DataTelepon

class AdminHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminHomeBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: DataTeleponAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Pemeriksaan status admin
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        if (!sharedPref.getBoolean("isAdmin", false)) {
            // Jika tidak dalam status admin, kembali ke HomeActivity atau LoginActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding.imageProfile.setOnClickListener {
            showLogoutDialog()
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("DataTelepon")
        adapter = DataTeleponAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Setup SearchView
        setupSearchView()
        setupItemTouchHelper()  // Memanggil method setupItemTouchHelper

        fetchDataFromDatabase()

        // Tambahkan onClickListener ke CardView untuk menuju ke AddDataActivity
        binding.btnAddData.setOnClickListener {
            val intent = Intent(this, AddDataActivity::class.java)
            startActivity(intent)
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

    private fun setupItemTouchHelper() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val dataTelepon = adapter.getItemAtPosition(position)
                showDeleteConfirmationDialog(dataTelepon, position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun showDeleteConfirmationDialog(dataTelepon: DataTelepon, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah anda yakin ingin menghapus kontak ${dataTelepon.namaKontak}?")
            .setPositiveButton("Hapus") { dialog, which ->
                deleteDataTelepon(dataTelepon, position)
            }
            .setNegativeButton("Batal") { dialog, which ->
                adapter.notifyItemChanged(position) // Mengembalikan item ke posisi semula jika penghapusan dibatalkan
                Toast.makeText(this, "Batal menghapus data.", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun deleteDataTelepon(dataTelepon: DataTelepon, position: Int) {
        databaseReference.child(dataTelepon.id).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Data berhasil dihapus.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menghapus data.", Toast.LENGTH_SHORT).show()
                adapter.notifyItemChanged(position) // Jika gagal, kembalikan item ke posisi semula
            }
        }
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
                    data?.id = dataSnapshot.key.toString()  // Mengatur id dengan key dari Firebase
                    data?.let {
                        // Ambil nilai namaKontak dan jabatan dari objek DataTelepon
                        val namaKontak = it.namaKontak
                        val jabatan = it.jabatan
                        // Tambahkan objek DataTelepon ke dataList
                        dataList.add(it)
                    }
                }
                // Setel data dataList ke adapter
                adapter.setData(dataList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdminHomeActivity", "Failed to read value.", error.toException())
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

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Apakah anda yakin ingin logout?")
            .setPositiveButton("Iya") { dialog, which ->
                // Hapus status admin dari SharedPreferences
                val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    remove("isAdmin") // atau menggunakan putBoolean("isAdmin", false) untuk mengatur kembali ke false
                    apply()
                }

                Toast.makeText(this, "Anda berhasil logout.", Toast.LENGTH_LONG).show()

                // Redirect ke HomeActivity dengan flags untuk membersihkan semua activity sebelumnya
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Tidak") { dialog, which ->
                Toast.makeText(this, "Anda batal logout.", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}