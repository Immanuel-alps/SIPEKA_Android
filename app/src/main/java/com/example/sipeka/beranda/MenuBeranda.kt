package com.example.sipeka.beranda

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/** Pertemuan 11 – data satu kotak menu pada grid Beranda */
data class MenuBeranda(
    val aksi: Aksi,
    @param:StringRes val judul: Int,
    @param:DrawableRes val ikon: Int,
    @param:ColorRes val warna: Int
) {
    /** Tujuan yang dibuka saat kotak menu diketuk */
    enum class Aksi { INPUT_PANEN, VALIDASI, RIWAYAT, DATA_PEMANEN, INFO_SAWIT, PANDUAN }
}
