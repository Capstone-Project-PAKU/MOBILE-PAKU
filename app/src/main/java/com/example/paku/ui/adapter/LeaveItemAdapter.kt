package com.example.paku.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.R
import com.example.paku.data.model.list.WorkLeaveData

class LeaveItemAdapter(private var itemList: List<WorkLeaveData>):
    RecyclerView.Adapter<LeaveItemAdapter.LeaveViewHolder>() {

    class LeaveViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val leaveTitleText: TextView = view.findViewById(R.id.tvLeaveTitle)
        val leaveDateText: TextView = view.findViewById(R.id.tvLeaveDate)
        val leaveStatusText: TextView = view.findViewById(R.id.tvLeaveStatus)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_pengajuan_cuti, parent, false)
        return LeaveViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        val item = itemList[position]
        val context = holder.itemView.context
        if (item.status_validasi == "disetujui") {
            holder.leaveStatusText.setTextColor(ContextCompat.getColor(context, R.color.approve))
        } else if (item.status_validasi == "ditolak") {
            holder.leaveStatusText.setTextColor(ContextCompat.getColor(context, R.color.red))
        } else {
            holder.leaveStatusText.setTextColor(ContextCompat.getColor(context, R.color.pending))
        }
        holder.leaveTitleText.text = "Pengajuan ${item.jenis_cuti}"
        holder.leaveStatusText.text = item.status_validasi
        holder.leaveDateText.text = "${item.tgl_awal_cuti} - ${item.tgl_akhir_cuti}"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateData(newList: List<WorkLeaveData>) {
        itemList = newList
        notifyDataSetChanged()
    }

}