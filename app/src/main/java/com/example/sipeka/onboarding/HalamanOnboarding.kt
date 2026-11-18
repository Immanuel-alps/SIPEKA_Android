package com.example.sipeka.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.sipeka.R

/** Pertemuan 11 – isi satu halaman On Board Screen */
data class HalamanOnboarding(
    @param:StringRes val label: Int,
    @param:StringRes val judul: Int,
    @param:StringRes val deskripsi: Int,
    @param:DrawableRes val ikon: Int,
    @param:DrawableRes val latar: Int
) {
    companion object {
        /** 3 halaman pengenalan SIPEKA: catat panen → hitung premi → validasi & slip */
        val SEMUA = listOf(
            HalamanOnboarding(
                R.string.onboard_label_1, R.string.onboard_judul_1, R.string.onboard_desk_1,
                R.drawable.ic_agriculture, R.drawable.bg_onboard_1
            ),
            HalamanOnboarding(
                R.string.onboard_label_2, R.string.onboard_judul_2, R.string.onboard_desk_2,
                R.drawable.ic_scale, R.drawable.bg_onboard_2
            ),
            HalamanOnboarding(
                R.string.onboard_label_3, R.string.onboard_judul_3, R.string.onboard_desk_3,
                R.drawable.ic_receipt_long, R.drawable.bg_onboard_3
            )
        )
    }
}
