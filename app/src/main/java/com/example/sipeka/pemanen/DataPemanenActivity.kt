package com.example.sipeka.pemanen

import android.os.Bundle
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.sipeka.R
import com.example.sipeka.TampilanHelper
import com.example.sipeka.data.DataPanen
import com.example.sipeka.data.Format
import com.example.sipeka.data.RepositoriPanen
import com.example.sipeka.databinding.ActivityDataPemanenBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Pertemuan 11 – SimpleAdapter: halaman Data Pemanen.
 * Menampilkan rekap premi setiap pemanen di ListView memakai SimpleAdapter.
 *
 * Sumber: https://developer.android.com/reference/android/widget/SimpleAdapter
 */
class DataPemanenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataPemanenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataPemanenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TampilanHelper.aturEdgeToEdge(this, binding.root, binding.appBar)

        // Toolbar + tombol back
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.judul_data_pemanen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tampilkanDaftarPemanen()
    }

    private fun tampilkanDaftarPemanen() {
        val semuaPanen = RepositoriPanen.semua()

        // 1. Siapkan data: satu Map (key → teks) untuk setiap baris
        val daftarBaris = RepositoriPanen.daftarPemanen.map { nama ->
            val panenPemanen = semuaPanen.filter { it.pemanen == nama }
            val menunggu = panenPemanen.count { it.status == DataPanen.STATUS_MENUNGGU }
            mapOf(
                KEY_INISIAL to Format.inisial(nama),
                KEY_NAMA to nama,
                KEY_DETAIL to getString(
                    R.string.detail_pemanen,
                    panenPemanen.size,
                    Format.kg(panenPemanen.sumOf { it.beratKg })
                ),
                KEY_PREMI to Format.rupiah(panenPemanen.sumOf { it.premi.total }),
                KEY_STATUS to when {
                    panenPemanen.isEmpty() -> getString(R.string.status_belum_panen)
                    menunggu > 0 -> getString(R.string.status_ada_menunggu, menunggu)
                    else -> getString(R.string.status_lunas)
                }
            )
        }

        // 2. SimpleAdapter memetakan key pada Map (from) ke id TextView di item_pemanen.xml (to)
        val from = arrayOf(KEY_INISIAL, KEY_NAMA, KEY_DETAIL, KEY_PREMI, KEY_STATUS)
        val to = intArrayOf(R.id.tvInisial, R.id.tvNama, R.id.tvDetail, R.id.tvPremi, R.id.tvStatus)
        val adapter = SimpleAdapter(this, daftarBaris, R.layout.item_pemanen, from, to)

        // 3. Pasang adapter ke ListView
        binding.listPemanen.adapter = adapter

        binding.tvRingkasan.text = getString(
            R.string.ringkasan_pemanen,
            RepositoriPanen.daftarPemanen.size,
            Format.rupiah(RepositoriPanen.totalPremi())
        )

        // 4. Baris diketuk → tampilkan rincian premi pemanen tersebut
        binding.listPemanen.setOnItemClickListener { _, _, posisi, _ ->
            tampilkanRincian(RepositoriPanen.daftarPemanen[posisi])
        }
    }

    private fun tampilkanRincian(nama: String) {
        val panenPemanen = RepositoriPanen.semua().filter { it.pemanen == nama }
        val dibayar = panenPemanen.filter { it.status == DataPanen.STATUS_DIBAYAR }.sumOf { it.premi.total }
        val menunggu = panenPemanen.filter { it.status == DataPanen.STATUS_MENUNGGU }.sumOf { it.premi.total }

        MaterialAlertDialogBuilder(this)
            .setTitle(nama)
            .setIcon(R.drawable.ic_person)
            .setMessage(
                getString(
                    R.string.rincian_pemanen,
                    panenPemanen.size,
                    Format.kg(panenPemanen.sumOf { it.beratKg }),
                    Format.rupiah(dibayar),
                    Format.rupiah(menunggu)
                )
            )
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val KEY_INISIAL = "inisial"
        private const val KEY_NAMA = "nama"
        private const val KEY_DETAIL = "detail"
        private const val KEY_PREMI = "premi"
        private const val KEY_STATUS = "status"
    }
}
