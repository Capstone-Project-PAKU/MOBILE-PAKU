package com.example.paku.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.R
import com.example.paku.data.model.list.PayrollData
import java.text.NumberFormat
import java.util.Locale

class PayrollItemAdapter(
    private var itemList: List<PayrollData>,
    private val onShowInvoicePhoto: (View, String?) -> Unit,
):
    RecyclerView.Adapter<PayrollItemAdapter.PayrollViewHolder>() {
    inner class PayrollViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val waktuText: TextView = view.findViewById(R.id.tvWaktuValue)
        val gajiPokokText: TextView = view.findViewById(R.id.tvGajiPokokValue)
        val jumlahLemburText: TextView = view.findViewById(R.id.tvJumlahLemburValue)
        val tunjanganText: TextView = view.findViewById(R.id.tvTunjanganValue)
        val totalGajiText: TextView = view.findViewById(R.id.tvTotalGajiValue)
        val metodePembayaranText: TextView = view.findViewById(R.id.tvMetodePembayaranValue)
        val buktiPembayaranText: TextView = view.findViewById(R.id.tvBuktiPembayaranValue)

        fun ShowInvoicePhoto(urlPath: String?) {
            if (urlPath != null) {
                buktiPembayaranText.text = "view"
                buktiPembayaranText.setTextColor(Color.BLUE)
                buktiPembayaranText.setOnClickListener {
                    onShowInvoicePhoto(itemView, urlPath)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayrollViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slip_gaji, parent, false)
        return PayrollViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PayrollViewHolder, position: Int) {
        val item = itemList[position]
        holder.waktuText.text = item.tanggal
        holder.gajiPokokText.text = "Rp.${rupiah(item.gaji_pokok)}"
        holder.jumlahLemburText.text = "${item.jam_lembur} Jam"
        holder.tunjanganText.text = "Rp.${rupiah(item.tunjangan)}"
        holder.totalGajiText.text = "RP.${rupiah(item.total_gaji)}"
        holder.metodePembayaranText.text = item.metode_pembayaran ?: "-"
        holder.ShowInvoicePhoto(item.bukti_pembayaran)
    }

    fun updateData(newList: List<PayrollData>) {
        itemList = newList
        notifyDataSetChanged()
    }

    private fun rupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getNumberInstance(localeID)
        return numberFormat.format(number).toString()
    }
}