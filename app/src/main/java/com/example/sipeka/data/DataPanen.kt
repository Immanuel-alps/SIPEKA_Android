package com.example.sipeka.data

/** Satu catatan hasil panen seorang pemanen */
data class DataPanen(
    val id: Int,
    val pemanen: String,
    val blok: String,
    val tanggal: String,
    val beratKg: Int,
    val grade: String,          // "A", "B", atau "C"
    val lahanSulit: Boolean,    // false = lahan datar, true = lahan sulit/berbukit
    val janjangMentah: Int,     // jumlah janjang buah mentah (kena denda)
    val premi: HasilPremi,
    var status: String = STATUS_MENUNGGU,
    var tandaTangan: String? = null   // path file PNG tanda tangan pemanen (diisi saat validasi)
) {
    companion object {
        const val STATUS_MENUNGGU = "Menunggu validasi"
        const val STATUS_DIBAYAR = "Dibayar"
    }
}
