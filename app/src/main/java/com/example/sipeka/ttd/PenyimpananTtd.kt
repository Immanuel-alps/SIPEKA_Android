package com.example.sipeka.ttd

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

/**
 * Menyimpan gambar tanda tangan pemanen ke penyimpanan internal aplikasi (files/ttd/).
 * Folder internal tidak butuh izin storage dan tidak bisa dibuka aplikasi lain.
 */
object PenyimpananTtd {

    /** @return path file PNG yang disimpan di DataPanen.tandaTangan */
    fun simpan(context: Context, bitmap: Bitmap, idPanen: Int): String {
        val folder = File(context.filesDir, "ttd").apply { mkdirs() }
        val file = File(folder, "ttd_panen_$idPanen.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return file.absolutePath
    }
}
