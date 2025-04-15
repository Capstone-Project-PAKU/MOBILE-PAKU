package com.example.paku.ui.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.R
import com.example.paku.data.model.recycleview.PresenceItem
import com.example.paku.ui.popup.MapPopupFragment

class PresenceItemAdapter(
    private var itemList: List<PresenceItem>,
    private val onShowClockInPhoto: (View, String?) -> Unit,
    private val onShowClockOutPhoto: (View, String?) -> Unit,
    private val onShowClockInLocation: (String?) -> Unit,
    private val onShowClockOutLocation: (String?) -> Unit
):
    RecyclerView.Adapter<PresenceItemAdapter.PresenceViewHolder>() {

    inner class PresenceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.tvTanggal)
        val clockInText: TextView = view.findViewById(R.id.tvClockedInValue)
        val clockOutText: TextView = view.findViewById(R.id.tvClockedOutValue)
        val lokasiMasukText: TextView = view.findViewById(R.id.tvLokasiMasukValue)
        val lokasiKeluarText: TextView = view.findViewById(R.id.tvLokasiKeluarValue)
        val FotoMasukText: TextView = view.findViewById(R.id.tvFotoMasukValue)
        val FotoKeluarText: TextView = view.findViewById(R.id.tvFotoKeluarValue)
        val statusText: TextView = view.findViewById(R.id.tvStatusValue)

        fun showClockInPhoto(clockInPhotoUrlPath: String?) {
            if (clockInPhotoUrlPath != null) {
                FotoMasukText.text = "view"
                FotoMasukText.setTextColor(Color.BLUE)
                FotoMasukText.setOnClickListener {
                    onShowClockInPhoto(itemView, clockInPhotoUrlPath)
                }
            }
        }

        fun showClockOutPhoto(clockOutPhotoPath: String?) {
            if (clockOutPhotoPath != null) {
                FotoKeluarText.text = "view"
                FotoKeluarText.setTextColor(Color.BLUE)
                FotoKeluarText.setOnClickListener {
                    onShowClockOutPhoto(itemView, clockOutPhotoPath)
                }
            }
        }

        fun showClockInLocation(location: String?) {
            if (location != null) {
                lokasiMasukText.text = "view"
                lokasiMasukText.setTextColor(Color.BLUE)
                lokasiMasukText.setOnClickListener {
                    onShowClockInLocation(location)
                }
            }
        }

        fun showClockOutLocation(location: String?) {
            if (location != null) {
                lokasiKeluarText.text = "view"
                lokasiKeluarText.setTextColor(Color.BLUE)
                lokasiKeluarText.setOnClickListener {
                    onShowClockOutLocation(location)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresenceViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_data_presensi, parent, false)
        return PresenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PresenceViewHolder, position: Int) {
        val item = itemList[position]
        val context = holder.itemView.context
        holder.dateText.text = item.tanggal_presensi
        holder.clockInText.text = item.waktu_masuk
        holder.clockOutText.text = item.waktu_keluar ?: "-"
        holder.showClockInLocation(item.lokasi_masuk)
        holder.showClockOutLocation(item.lokasi_keluar)

        holder.showClockInPhoto(item.foto_selfie_masuk?.let {
            transformCloudinaryUrl(it)
        })
        holder.showClockOutPhoto(item.foto_selfie_keluar?.let {
            transformCloudinaryUrl(it)
        })
        holder.statusText.text = setStatus(item.validasi_masuk, item.validasi_keluar)
        changeStatusColor(context, holder.statusText)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private fun transformCloudinaryUrl(url: String, width: Int = 350, height: Int = 350): String {
        return url.replace("/upload/", "/upload/w_${width},h_${height},c_fill/")
    }

    private fun changeStatusColor(context: Context, textView: TextView) {
        if (textView.text == "setuju") {
            textView.setTextColor(ContextCompat.getColor(context, R.color.approve))
        } else if (textView.text == "tolak") {
            textView.setTextColor(ContextCompat.getColor(context, R.color.red))
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.pending))
        }
    }

    private fun setStatus(status1: String, status2: String): String {
        if (status1 == "setuju" && status2 == "setuju") {
            return "setuju"
        } else if (status1 == "tolak" || status2 == "tolak") {
            return "tolak"
        } else {
            return "pending"
        }
    }

}