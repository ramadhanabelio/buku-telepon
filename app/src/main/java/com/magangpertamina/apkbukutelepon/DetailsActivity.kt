package com.magangpertamina.apkbukutelepon

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.magangpertamina.apkbukutelepon.databinding.ActivityDetailsBinding
import com.magangpertamina.apkbukutelepon.modeldata.DataTelepon
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var dataTelepon: DataTelepon? = null  // Menyimpan referensi DataTelepon di level class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup action bar
        setupActionBar()

        // Mendapatkan objek DataTelepon dari intent
        val dataTelepon = intent.getSerializableExtra("DATA_TELEPON") as? DataTelepon
        this.dataTelepon = dataTelepon

        if (dataTelepon != null) {
            // Menetapkan nilai ke TextView sesuai dengan data yang diambil
            binding.tvName.text = dataTelepon.namaKontak
            binding.tvJob.text = dataTelepon.jabatan
            binding.tvNumberJob.text = dataTelepon.noPekerja
            binding.textViewIsiJudul.text = dataTelepon.bagian
            binding.textViewIsiJudul2.text = dataTelepon.alamat
            binding.textViewIsiJudul3.text = dataTelepon.telpRumah
            binding.textViewIsiJudul4.text = dataTelepon.telpKantor
        } else {
            finish()
        }
    }

    private fun setupActionBar() {
        val actionBarColor = "#006CB8".toColorInt()
        supportActionBar?.setBackgroundDrawable(actionBarColor.toDrawable())
        val titleColor = Color.WHITE
        supportActionBar?.title = Html.fromHtml("<font color='$titleColor'>Detail Kontak</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_edit -> {
                if (isAdmin()) {  // Memastikan hanya admin yang bisa mengakses fungsi edit
                    dataTelepon?.let {
                        val intent = Intent(this, UpdateDataActivity::class.java)
                        intent.putExtra("DATA_ID", it.id)
                        startActivityForResult(intent, REQUEST_CODE_EDIT)
                    }
                    return true
                } else {
                    Toast.makeText(this, "Anda tidak memiliki akses untuk mengedit data ini.", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            // Reload the updated data
            dataTelepon?.id?.let { reloadUpdatedData(it) }
        }
    }

    private fun displayData(data: DataTelepon) {
        binding.tvName.text = data.namaKontak
        binding.tvJob.text = data.jabatan
        binding.tvNumberJob.text = data.noPekerja
        binding.textViewIsiJudul.text = data.bagian
        binding.textViewIsiJudul2.text = data.alamat
        binding.textViewIsiJudul3.text = data.telpRumah
        binding.textViewIsiJudul4.text = data.telpKantor
    }

    private fun reloadUpdatedData(dataId: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("DataTelepon")
        databaseRef.child(dataId).get().addOnSuccessListener { dataSnapshot ->
            val updatedData = dataSnapshot.getValue(DataTelepon::class.java)
            if (updatedData != null) {
                dataTelepon = updatedData  // Perbarui dataTelepon
                displayData(updatedData)  // Perbarui UI dengan data terbaru
            } else {
                Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_LONG).show()
        }
    }

private fun isAdmin(): Boolean {
    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
    return sharedPref.getBoolean("isAdmin", false)
}

    companion object {
        private const val REQUEST_CODE_EDIT = 1
    }
}