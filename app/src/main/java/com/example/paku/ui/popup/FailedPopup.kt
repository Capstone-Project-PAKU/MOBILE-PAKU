package com.example.paku.ui.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.example.paku.R
fun showFailedPopup(view: View, message: String?) {
    val inflater = LayoutInflater.from(view.context)
    val popupView = inflater.inflate(R.layout.popup_gagal, null)

    val popupWindow = PopupWindow(
        popupView,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )

    val backBtn = popupView.findViewById<Button>(R.id.failedBackBtn)
    val caption = popupView.findViewById<TextView>(R.id.failedPopupTv)

    if (message != null){
        caption.text = message
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

    backBtn.setOnClickListener {
        popupWindow.dismiss()
    }

    popupWindow.showAtLocation(view, android.view.Gravity.CENTER, 0, 0)
}