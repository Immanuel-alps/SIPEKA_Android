package com.example.sipeka.data

import java.text.NumberFormat
import java.util.Locale

object Format {
    private val lokal = Locale.forLanguageTag("id-ID")

    /** 286000 → "Rp286.000" */
    fun rupiah(nilai: Long): String {
        val format = NumberFormat.getCurrencyInstance(lokal)
        format.maximumFractionDigits = 0
        return format.format(nilai)
    }

    /** 1820 → "1.820 kg" */
    fun kg(berat: Int): String = NumberFormat.getIntegerInstance(lokal).format(berat) + " kg"

    /** "Andi Saputra" → "AS" */
    fun inisial(nama: String): String =
        nama.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
}
