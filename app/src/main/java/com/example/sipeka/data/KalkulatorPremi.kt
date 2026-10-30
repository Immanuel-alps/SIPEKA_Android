package com.example.sipeka.data

/** Hasil perhitungan premi, dipakai di form Input Panen, Validasi, dan Riwayat */
data class HasilPremi(
    val premiDasar: Long,
    val bonusMutu: Long,
    val denda: Long,
    val total: Long
)

/**
 * Rumus premi SIPEKA:
 * premi bersih = (berat × tarif/kg × koefisien lahan) + bonus mutu − denda buah mentah
 */
object KalkulatorPremi {

    const val TARIF_PER_KG = 145L          // Rp per kg TBS
    const val KOEF_DATAR = 1.0
    const val KOEF_SULIT = 1.2             // lahan sulit/berbukit dapat premi 20% lebih besar
    const val DENDA_PER_JANJANG = 2_500L   // Rp per janjang buah mentah

    /** bonus mutu per kg sesuai grade buah */
    fun bonusPerKg(grade: String): Long = when (grade) {
        "A" -> 12L
        "B" -> 6L
        else -> 0L
    }

    fun hitung(beratKg: Int, lahanSulit: Boolean, grade: String, janjangMentah: Int): HasilPremi {
        val koefisien = if (lahanSulit) KOEF_SULIT else KOEF_DATAR
        val premiDasar = (beratKg * TARIF_PER_KG * koefisien).toLong()
        val bonusMutu = beratKg * bonusPerKg(grade)
        val denda = janjangMentah * DENDA_PER_JANJANG
        val total = (premiDasar + bonusMutu - denda).coerceAtLeast(0L)
        return HasilPremi(premiDasar, bonusMutu, denda, total)
    }
}
