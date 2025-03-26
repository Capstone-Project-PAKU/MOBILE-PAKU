package com.example.paku.ui.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.paku.PresensiDalamAlternatifFragment
import com.example.paku.R

fun showPresenceFailedPopup(view: View, message: String?, state: Boolean = true) {
    val inflater = LayoutInflater.from(view.context)
    val popupView = inflater.inflate(R.layout.popup_presensi_wifi_ditolak, null)

    val popupWindow = PopupWindow(
        popupView,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )

    val presenceAltBtn = popupView.findViewById<Button>(R.id.presenceAltBtn)
    val backBtn = popupView.findViewById<Button>(R.id.backBtn)
    val caption = popupView.findViewById<TextView>(R.id.failedPresencePopupTv)

    if (message != null){
        caption.text = message
    }

    if (!state) {
        presenceAltBtn.visibility = View.INVISIBLE
    }

    popupWindow.isFocusable = true
    popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    popupWindow.isOutsideTouchable = false

    // dim the background when the popup is shown
    val window = view.context as? android.app.Activity
    val params = window?.window?.attributes
    params?.alpha = 0.5f
    window?.window?.attributes = params

    popupWindow.setOnDismissListener {
        params?.alpha = 1.0f
        window?.window?.attributes = params
    }

    presenceAltBtn.setOnClickListener {
        val activity = view.context as FragmentActivity
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, PresensiDalamAlternatifFragment())
        transaction.addToBackStack(null)
        transaction.commit()

        popupWindow.dismiss()
    }

    backBtn.setOnClickListener {
        popupWindow.dismiss()
    }

    popupWindow.showAtLocation(view, android.view.Gravity.CENTER, 0, 0)
}
