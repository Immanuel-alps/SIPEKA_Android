package com.example.sipeka.data

/**
 * Tempat menyimpan data panen selama aplikasi berjalan (sementara, di memori).
 * Nanti diganti dengan database Room / API server.
 */
object RepositoriPanen {

    val daftarPemanen = listOf("Andi Saputra", "Budi Rahman", "Siti Aminah", "Joko Prasetyo", "Rudi Hartono")
    val daftarBlok = listOf("A-03", "B-17", "C-08", "C-12", "D-05")
    val daftarGrade = listOf("A", "B", "C")

    private var idTerakhir = 0
    private val semuaData = mutableListOf<DataPanen>()

    init {
        // data contoh agar halaman Beranda, Validasi, dan Riwayat tidak kosong
        tambah("Siti Aminah", "B-17", "28 Sep 2026", 1540, "A", false, 0, DataPanen.STATUS_DIBAYAR)
        tambah("Joko Prasetyo", "A-03", "29 Sep 2026", 1675, "B", true, 2, DataPanen.STATUS_DIBAYAR)
        tambah("Budi Rahman", "C-08", "30 Sep 2026", 1640, "B", true, 1)
        tambah("Andi Saputra", "C-12", "30 Sep 2026", 1820, "A", false, 0)
    }

    fun tambah(
        pemanen: String, blok: String, tanggal: String, beratKg: Int, grade: String,
        lahanSulit: Boolean, janjangMentah: Int, status: String = DataPanen.STATUS_MENUNGGU
    ): DataPanen {
        val premi = KalkulatorPremi.hitung(beratKg, lahanSulit, grade, janjangMentah)
        idTerakhir++
        val data = DataPanen(idTerakhir, pemanen, blok, tanggal, beratKg, grade, lahanSulit, janjangMentah, premi, status)
        semuaData.add(0, data) // data terbaru di paling atas
        return data
    }

    fun semua(): List<DataPanen> = semuaData

    fun menungguValidasi(): List<DataPanen> = semuaData.filter { it.status == DataPanen.STATUS_MENUNGGU }

    /** Tandai dibayar + simpan path tanda tangan digital pemanen (fitur tambahan Pertemuan 11) */
    fun validasi(id: Int, pathTandaTangan: String?) {
        semuaData.find { it.id == id }?.apply {
            status = DataPanen.STATUS_DIBAYAR
            tandaTangan = pathTandaTangan
        }
    }

    fun totalPremi(): Long = semuaData.sumOf { it.premi.total }

    fun totalPremiDibayar(): Long =
        semuaData.filter { it.status == DataPanen.STATUS_DIBAYAR }.sumOf { it.premi.total }

    fun jumlahPemanenAktif(): Int = semuaData.map { it.pemanen }.distinct().size
}
