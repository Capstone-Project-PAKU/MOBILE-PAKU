package com.example.paku.data.model.users

import android.provider.ContactsContract.CommonDataKinds.Email

data class RegisterRequest(
    val id_pegawai: String,
    val username: String,
    val email: String,
    val password: String,
    val role: String,
    val id_android: String
)
