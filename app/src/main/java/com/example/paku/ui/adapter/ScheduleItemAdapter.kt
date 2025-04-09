package com.example.paku.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.R
import com.example.paku.data.model.list.ScheduleDetailData

class ScheduleItemAdapter(private var itemList: List<ScheduleDetailData>):
    RecyclerView.Adapter<ScheduleItemAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.tvTanggal)
        val shiftKerjaText: TextView = view.findViewById(R.id.tvShiftKerjaValue)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal_kerja, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = itemList[position]
        holder.dateText.text = item.tanggal
        holder.shiftKerjaText.text = convertJadwal(item.shift_kerja)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private fun convertJadwal(s: String): String? {
        val jadwal = mapOf("P" to "Pagi", "S" to "Siang", "F" to "Full", "L" to "Libur")
        return jadwal[s]
    }

    fun updateData(newList: List<ScheduleDetailData>) {
        itemList = newList
        notifyDataSetChanged()
    }

}