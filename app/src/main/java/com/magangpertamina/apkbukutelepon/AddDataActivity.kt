package com.magangpertamina.apkbukutelepon

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.magangpertamina.apkbukutelepon.modeldata.DataTelepon
import com.magangpertamina.apkbukutelepon.databinding.ActivityAddDataBinding
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable

class AddDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup action bar
        setupActionBar()

        // Tombol Kirim onClickListener
        binding.btnKirim.setOnClickListener {
            submitDataToDatabase()
        }
    }

    private fun setupActionBar() {
        val actionBarColor = "#006CB8".toColorInt()
        supportActionBar?.setBackgroundDrawable(actionBarColor.toDrawable())
        val titleColor = Color.WHITE
        supportActionBar?.title = Html.fromHtml("<font color='$titleColor'>Tambah Data</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Arahkan ke halaman AdminHomeActivity
                val intent = Intent(this, AdminHomeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun submitDataToDatabase() {
        // Tampilkan ProgressBar
        binding.progressBar.visibility = View.VISIBLE

        val namaKontak = binding.namaKontak.text.toString().trim()
        val noPekerja = binding.noPekerja.text.toString().trim()
        val jabatan = binding.jabatan.text.toString().trim()
        val bagian = binding.bagian.text.toString().trim()
        val alamat = binding.alamat.text.toString().trim()
        val telpRumah = binding.telpRumah.text.toString().trim()
        val telpKantor = binding.telpKantor.text.toString().trim()

        // Referensi ke database
        val databaseRef = FirebaseDatabase.getInstance().getReference("DataTelepon")
        val dataId = databaseRef.push().key

        if (dataId != null) {
            val data = DataTelepon(namaKontak, noPekerja, jabatan, bagian, alamat, telpRumah, telpKantor).apply {
                this.id = dataId  // Set id here
            }
            databaseRef.child(dataId).setValue(data)
                .addOnCompleteListener { task ->
                    binding.progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Data berhasil disimpan.", Toast.LENGTH_SHORT).show()

                        // Mengosongkan semua inputan setelah data berhasil disimpan
                        clearInputFields()
                    } else {
                        Toast.makeText(this, "Gagal menyimpan data.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Gagal mendapatkan ID data unik.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputFields() {
        binding.namaKontak.setText("")
        binding.noPekerja.setText("")
        binding.jabatan.setText("")
        binding.bagian.setText("")
        binding.alamat.setText("")
        binding.telpRumah.setText("")
        binding.telpKantor.setText("")
    }

}