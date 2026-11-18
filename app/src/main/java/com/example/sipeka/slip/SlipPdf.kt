package com.example.sipeka.slip

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.sipeka.R
import com.example.sipeka.data.DataPanen
import com.example.sipeka.data.Format
import com.example.sipeka.data.KalkulatorPremi
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pertemuan 9 – Fitur 3: membuat slip premi pemanen dalam bentuk PDF lalu dibagikan.
 *
 * Sumber:
 * - PdfDocument (Android Developers): https://developer.android.com/reference/android/graphics/pdf/PdfDocument
 * - Generate PDF File in Android using Kotlin (GeeksforGeeks): https://www.geeksforgeeks.org/generate-pdf-file-in-android-using-kotlin/
 * - FileProvider (Android Developers): https://developer.android.com/reference/androidx/core/content/FileProvider
 * - Sending simple data to other apps (Android Developers): https://developer.android.com/training/sharing/send
 */
object SlipPdf {

    // Ukuran kertas A5 dalam satuan point (1 point = 1/72 inci): 420 x 595
    private const val LEBAR = 420
    private const val TINGGI = 595
    private const val MARGIN = 28f

    /** Membuat file PDF slip premi untuk satu data panen, disimpan di cache aplikasi */
    fun buat(context: Context, data: DataPanen, namaAdmin: String): File {
        // 1. Buat dokumen PDF dengan 1 halaman A5
        val dokumen = PdfDocument()
        val infoHalaman = PdfDocument.PageInfo.Builder(LEBAR, TINGGI, 1).create()
        val halaman = dokumen.startPage(infoHalaman)

        // 2. Gambar isi slip di Canvas halaman (sama seperti menggambar di View)
        gambarSlip(context, halaman.canvas, data, namaAdmin)
        dokumen.finishPage(halaman)

        // 3. Simpan ke folder cache/slip → nanti dibagikan lewat FileProvider
        val folder = File(context.cacheDir, "slip").apply { mkdirs() }
        val file = File(folder, "Slip_Premi_${data.pemanen.replace(" ", "_")}_${data.id}.pdf")
        FileOutputStream(file).use { dokumen.writeTo(it) }
        dokumen.close()
        return file
    }

    /** Membuka menu "Bagikan" Android (WhatsApp, Gmail, Drive, dll.) */
    fun bagikan(context: Context, file: File) {
        val uri = uriDari(context, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.slip_subjek))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.slip_bagikan_judul)))
    }

    /** Membuka PDF di aplikasi pembaca PDF yang ada di HP */
    fun buka(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uriDari(context, file), "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    // content://com.example.sipeka.fileprovider/slip/... (bukan file:// supaya aman dibaca aplikasi lain)
    private fun uriDari(context: Context, file: File) =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    // ================= Menggambar isi slip =================

    private fun gambarSlip(context: Context, c: Canvas, data: DataPanen, namaAdmin: String) {
        val warna = { id: Int -> ContextCompat.getColor(context, id) }
        val lora = ResourcesCompat.getFont(context, R.font.lora_bold)
        val inter = ResourcesCompat.getFont(context, R.font.inter_regular)
        val interTebal = ResourcesCompat.getFont(context, R.font.inter_bold)

        val teks = Paint(Paint.ANTI_ALIAS_FLAG).apply { typeface = inter; textSize = 11f; color = warna(R.color.teks_utama) }
        val label = Paint(teks).apply { color = warna(R.color.teks_sekunder) }
        val tebal = Paint(teks).apply { typeface = interTebal }
        val kanan = Paint(tebal).apply { textAlign = Paint.Align.RIGHT }
        val garis = Paint().apply { color = warna(R.color.garis); strokeWidth = 1f }
        val blok = Paint(Paint.ANTI_ALIAS_FLAG)

        // --- Header maroon + logo ---
        blok.color = warna(R.color.maroon_tua)
        c.drawRect(0f, 0f, LEBAR.toFloat(), 92f, blok)
        val logo = BitmapFactory.decodeResource(context.resources, R.drawable.logo_sipeka)
        c.drawBitmap(logo, null, Rect(MARGIN.toInt(), 22, MARGIN.toInt() + 48, 70), null)
        c.drawText("SIPEKA", MARGIN + 60, 48f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = lora; textSize = 22f; color = warna(R.color.putih_susu)
        })
        c.drawText(context.getString(R.string.slip_kop), MARGIN + 60, 66f, Paint(teks).apply {
            textSize = 10f; color = warna(R.color.emas_premi)
        })

        // --- Judul slip ---
        var y = 128f
        c.drawText(context.getString(R.string.slip_judul), MARGIN, y, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = lora; textSize = 17f; color = warna(R.color.maroon_sawit)
        })
        c.drawText("No. SLP-%04d".format(data.id), LEBAR - MARGIN, y, Paint(kanan).apply { color = warna(R.color.teks_sekunder) })
        y += 14f
        c.drawLine(MARGIN, y, LEBAR - MARGIN, y, garis)

        // --- Data panen ---
        val baris = { lbl: String, nilai: String, p: Paint ->
            y += 22f
            c.drawText(lbl, MARGIN, y, label)
            c.drawText(nilai, LEBAR - MARGIN, y, p)
        }
        val koef = if (data.lahanSulit) "Sulit / berbukit (×${KalkulatorPremi.KOEF_SULIT})" else "Datar (×${KalkulatorPremi.KOEF_DATAR})"
        baris("Nama pemanen", data.pemanen, kanan)
        baris("Tanggal panen", data.tanggal, kanan)
        baris("Blok", data.blok, kanan)
        baris("Berat bersih", Format.kg(data.beratKg), kanan)
        baris("Mutu buah", "Grade ${data.grade}", kanan)
        baris("Kondisi lahan", koef, kanan)
        baris("Janjang mentah", "${data.janjangMentah} janjang", kanan)

        // --- Rincian premi ---
        y += 18f
        c.drawLine(MARGIN, y, LEBAR - MARGIN, y, garis)
        y += 22f
        c.drawText(context.getString(R.string.slip_rincian), MARGIN, y, tebal)
        baris("Premi dasar", Format.rupiah(data.premi.premiDasar), kanan)
        baris("Bonus mutu", "+ " + Format.rupiah(data.premi.bonusMutu), kanan)
        baris("Denda buah mentah", "− " + Format.rupiah(data.premi.denda), Paint(kanan).apply { color = warna(R.color.jingga_brondolan) })

        // --- Kotak total ---
        y += 20f
        blok.color = warna(R.color.kuning_janjang)
        c.drawRoundRect(RectF(MARGIN, y, LEBAR - MARGIN, y + 52f), 10f, 10f, blok)
        c.drawText(context.getString(R.string.slip_total), MARGIN + 14, y + 31f, tebal)
        c.drawText(Format.rupiah(data.premi.total), LEBAR - MARGIN - 14, y + 33f, Paint(kanan).apply {
            typeface = lora; textSize = 18f; color = warna(R.color.maroon_tua)
        })
        y += 62f
        val kecil = Paint(label).apply { textSize = 9f }

        // --- Status (kiri) + tanda tangan digital pemanen (kanan) – fitur tambahan Pertemuan 11 ---
        c.drawText("Status pembayaran", MARGIN, y + 12f, label)
        c.drawText(data.status, MARGIN, y + 28f, Paint(tebal).apply { color = warna(R.color.hijau_sinkron) })
        gambarTandaTangan(c, data, RectF(LEBAR - MARGIN - 140f, y - 6f, LEBAR - MARGIN, y + 34f), garis, kecil)

        // --- Footer ---
        val waktu = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID")).format(Date())
        c.drawLine(MARGIN, TINGGI - 48f, LEBAR - MARGIN, TINGGI - 48f, garis)
        c.drawText(context.getString(R.string.slip_dicetak, waktu, namaAdmin), MARGIN, TINGGI - 32f, kecil)
        c.drawText(context.getString(R.string.slip_rumus), MARGIN, TINGGI - 19f, kecil)
    }

    /** Menggambar tanda tangan pemanen (PNG) di dalam kotak, ukurannya disesuaikan tanpa merusak rasio */
    private fun gambarTandaTangan(c: Canvas, data: DataPanen, kotak: RectF, garis: Paint, kecil: Paint) {
        val ttd = data.tandaTangan?.let { BitmapFactory.decodeFile(it) }
        if (ttd != null) {
            val skala = minOf(kotak.width() / ttd.width, kotak.height() / ttd.height)
            val lebar = ttd.width * skala
            val tinggi = ttd.height * skala
            val kiri = kotak.centerX() - lebar / 2
            val atas = kotak.bottom - tinggi
            c.drawBitmap(ttd, null, RectF(kiri, atas, kiri + lebar, atas + tinggi), null)
        }
        c.drawLine(kotak.left, kotak.bottom + 2f, kotak.right, kotak.bottom + 2f, garis)
        val tengah = Paint(kecil).apply { textAlign = Paint.Align.CENTER }
        val keterangan = if (ttd != null) "Tanda tangan ${data.pemanen}" else "Tanpa tanda tangan digital"
        c.drawText(keterangan, kotak.centerX(), kotak.bottom + 13f, tengah)
    }
}
