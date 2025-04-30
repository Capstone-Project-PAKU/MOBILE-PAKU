package com.example.paku.ui.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.paku.R

fun showPhotoViewer(view: View, header: String, urlpath: String?) {
    val inflater = LayoutInflater.from(view.context)
    val popupView = inflater.inflate(R.layout.popup_cek_photo, null)

    Log.d("POPUP", "Called showPhotoViewer with $urlpath")
    val popupWindow = PopupWindow(
        popupView,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )

    val backBtn = popupView.findViewById<Button>(R.id.backBtn)
    val photoPreview = popupView.findViewById<ImageView>(R.id.photoPreview)
    val photoHeader = popupView.findViewById<TextView>(R.id.photoHeader)

    photoHeader.text = header

    popupWindow.isFocusable = true
    popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    popupWindow.isOutsideTouchable = false

    // dim the background when the popup is shown
    val window = view.context as? android.app.Activity
    val params = window?.window?.attributes
    params?.alpha = 0.5f
    window?.window?.attributes = params

    // show image
    Glide.with(popupView)
        .load(urlpath)
        .into(photoPreview)

    popupWindow.setOnDismissListener {
        params?.alpha = 1.0f
        window?.window?.attributes = params
    }

    backBtn.setOnClickListener {
        popupWindow.dismiss()
    }

    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
}

