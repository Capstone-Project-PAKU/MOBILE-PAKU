package com.example.paku.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.R
import com.example.paku.data.model.list.WorkingRecapData

class PresenceRecapItemAdapter(private var itemList: List<WorkingRecapData>, private val onItemClick: (String, String) -> Unit):
    RecyclerView.Adapter<PresenceRecapItemAdapter.PresenceRecapViewHolder>() {

    inner class PresenceRecapViewHolder(view: View): RecyclerView.ViewHolder(view){
        val monthText: TextView = view.findViewById(R.id.tvWaktuValue)
        val KuotaText: TextView = view.findViewById(R.id.tvKuotaCutiValue)
        val presenceText: TextView = view.findViewById(R.id.tvTotalHadirValue)
        val absenceText: TextView = view.findViewById(R.id.tvTotalTidakHadirValue)
        val sickText: TextView = view.findViewById(R.id.tvSakitValue)
        val leaveText: TextView = view.findViewById(R.id.tvIzinValue)
        val alphaText: TextView = view.findViewById(R.id.tvTanpaKeteranganValue)
        val detailBtn: Button = view.findViewById(R.id.btnDetailPresensi)

        fun bind(month: String, year: String) {
            detailBtn.setOnClickListener {
                onItemClick(month, year)
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PresenceRecapViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cek_rekap_kehadiran, parent, false)
        return PresenceRecapViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PresenceRecapViewHolder,
        position: Int
    ) {
        val item = itemList[position]
        holder.monthText.text = "${convertToMonth(item.bulan)} ${item.tahun}"
        holder.KuotaText.text = "${item.sisa_kuota_libur}x"
        holder.presenceText.text = "${item.total_hadir}"
        holder.absenceText.text = "${item.total_absen}"
        holder.sickText.text = "${item.total_sakit}"
        holder.leaveText.text = "${item.total_libur}"
        holder.alphaText.text = "${item.tanpa_keterangan}"
        holder.bind(item.bulan, item.tahun)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateData(newList: List<WorkingRecapData>) {
        itemList = newList
        notifyDataSetChanged()
    }

    private fun convertToMonth(num: String): String? {
        val monthMap = mapOf(
            "1" to "Januari", "2" to "Februari", "3" to "Maret",
            "4" to "April", "5" to "Mei", "6" to "Juni",
            "7" to "Juli", "8" to "Agustus", "9" to "September",
            "10" to "Oktober", "11" to "November", "12" to "Desember"
        )

        return monthMap[num]
    }

}