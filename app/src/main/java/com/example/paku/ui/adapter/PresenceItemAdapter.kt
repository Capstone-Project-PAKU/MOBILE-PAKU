package com.example.paku.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.R
import com.example.paku.data.model.recycleview.PresenceItem

class PresenceItemAdapter(private var itemList: List<PresenceItem>):
    RecyclerView.Adapter<PresenceItemAdapter.PresenceViewHolder>() {

    class PresenceViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.tvTanggal)
        val clockInText: TextView = view.findViewById(R.id.tvClockedInValue)
        val clockOutText: TextView = view.findViewById(R.id.tvClockedOutValue)
        val lokasiText: TextView = view.findViewById(R.id.tvLokasiValue)
        val statusText: TextView = view.findViewById(R.id.tvStatusValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresenceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data_presensi, parent, false)
        return PresenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PresenceViewHolder, position: Int) {
        val item = itemList[position]
        holder.dateText.text = item.tanggal_presensi
        holder.clockInText.text = item.waktu_masuk
        holder.clockOutText.text = item.waktu_keluar
        holder.lokasiText.text = item.lokasi_masuk
        holder.statusText.text = item.validasi_masuk
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}