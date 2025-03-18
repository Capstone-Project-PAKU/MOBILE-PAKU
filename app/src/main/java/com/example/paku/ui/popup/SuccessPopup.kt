package com.example.paku.ui.popup

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.example.paku.R

fun showPresenceSuccessPopup(view: View, message: String) {
    val inflater = LayoutInflater.from(view.context)
    val popupView = inflater.inflate(R.layout.popup_presensi_berhasil, null)

    val popupWindow = PopupWindow(
        popupView,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )

    val caption = popupView.findViewById<TextView>(R.id.successPresencePopupTv)
    val finish = popupView.findViewById<Button>(R.id.finishSuccessPresenceBtn)

    caption.text = message

    // dim the background when the popup is shown
    val window = view.context as? android.app.Activity
    val params = window?.window?.attributes
    params?.alpha = 0.5f
    window?.window?.attributes = params

    finish.setOnClickListener {
        params?.alpha = 1.0f
        window?.window?.attributes = params
        popupWindow.dismiss()
    }

    popupWindow.showAtLocation(view, android.view.Gravity.CENTER, 0, 0)
}