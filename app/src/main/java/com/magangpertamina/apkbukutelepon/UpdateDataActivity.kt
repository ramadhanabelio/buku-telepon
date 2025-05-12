package com.magangpertamina.apkbukutelepon

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.magangpertamina.apkbukutelepon.databinding.ActivityUpdateDataBinding
import com.magangpertamina.apkbukutelepon.modeldata.DataTelepon
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable

class UpdateDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup action bar
        setupActionBar()

        val dataTeleponId = intent.getStringExtra("DATA_ID")
        if (dataTeleponId != null) {
            loadData(dataTeleponId)
        }

        binding.btnKirim.setOnClickListener {
            if (dataTeleponId != null) {
                updateData(dataTeleponId)
            }
        }
    }

    private fun setupActionBar() {
        val actionBarColor = "#006CB8".toColorInt()
        supportActionBar?.setBackgroundDrawable(actionBarColor.toDrawable())
        val titleColor = Color.WHITE
        supportActionBar?.title = Html.fromHtml("<font color='$titleColor'>Update Data</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Kembali ke activity sebelumnya
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData(id: String) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val databaseReference = FirebaseDatabase.getInstance().getReference("DataTelepon")
        databaseReference.child(id).get().addOnSuccessListener { dataSnapshot ->
            progressBar.visibility = View.GONE
            val dataTelepon = dataSnapshot.getValue(DataTelepon::class.java)
            if (dataTelepon != null) {
                with(binding) {
                    namaKontak.setText(dataTelepon.namaKontak)
                    noPekerja.setText(dataTelepon.noPekerja)
                    jabatan.setText(dataTelepon.jabatan)
                    bagian.setText(dataTelepon.bagian)
                    alamat.setText(dataTelepon.alamat)
                    telpRumah.setText(dataTelepon.telpRumah)
                    telpKantor.setText(dataTelepon.telpKantor)
                }
            }
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Failed to load data: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateData(id: String) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val updatedData = DataTelepon(
            namaKontak = binding.namaKontak.text.toString(),
            noPekerja = binding.noPekerja.text.toString(),
            jabatan = binding.jabatan.text.toString(),
            bagian = binding.bagian.text.toString(),
            alamat = binding.alamat.text.toString(),
            telpRumah = binding.telpRumah.text.toString(),
            telpKantor = binding.telpKantor.text.toString(),
            id = id  // Pastikan field ID ini diisi untuk memastikan update data yang benar
        )

        val databaseReference = FirebaseDatabase.getInstance().getReference("DataTelepon")
        databaseReference.child(id).setValue(updatedData).addOnSuccessListener {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Failed to update data: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}