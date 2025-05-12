package com.magangpertamina.apkbukutelepon.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.magangpertamina.apkbukutelepon.DetailsActivity
import com.magangpertamina.apkbukutelepon.R
import com.magangpertamina.apkbukutelepon.modeldata.DataTelepon

class DataTeleponAdapter(private var listData: List<DataTelepon>) : RecyclerView.Adapter<DataTeleponAdapter.DataViewHolder>() {
    // A list to store all data items
    private var fullListData: List<DataTelepon> = listData.toList()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dataTelepon: DataTelepon) {
            val tvName: TextView = itemView.findViewById(R.id.tvName)
            val tvPosition: TextView = itemView.findViewById(R.id.tvPosition)
            val tvPartContent: TextView = itemView.findViewById(R.id.tvPartContent)

            tvName.text = dataTelepon.namaKontak
            tvPosition.text = dataTelepon.telpKantor
            tvPartContent.text = dataTelepon.bagian

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetailsActivity::class.java).apply {
                    putExtra("DATA_TELEPON", dataTelepon)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount() = listData.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: List<DataTelepon>) {
        // Urutkan newData berdasarkan namaKontak sebelum menetapkannya ke listData
        listData = newData.sortedBy { it.namaKontak }
        fullListData = listData.toList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterData(text: String) {
        listData = if (text.isEmpty()) {
            fullListData
        } else {
            fullListData.filter {
                it.namaKontak.contains(text, ignoreCase = true) || it.telpKantor.contains(text, ignoreCase = true) || it.bagian.contains(text, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun getItemAtPosition(position: Int): DataTelepon {
        return listData[position]
    }
}