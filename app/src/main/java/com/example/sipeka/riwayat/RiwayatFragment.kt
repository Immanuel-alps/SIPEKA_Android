package com.example.sipeka.riwayat

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.example.sipeka.R
import com.example.sipeka.SessionManager
import com.example.sipeka.data.DataPanen
import com.example.sipeka.data.Format
import com.example.sipeka.data.RepositoriPanen
import com.example.sipeka.databinding.FragmentRiwayatBinding
import com.example.sipeka.slip.SlipPdf
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Halaman Riwayat: rekap semua premi + filter status */
class RiwayatFragment : Fragment() {

    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RiwayatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.judul_riwayat)
        }

        val semua = RepositoriPanen.semua()
        val dibayar = RepositoriPanen.totalPremiDibayar()
        binding.tvTotalSemua.text = Format.rupiah(RepositoriPanen.totalPremi())
        binding.tvDibayar.text = Format.rupiah(dibayar)
        binding.tvMenunggu.text = Format.rupiah(RepositoriPanen.totalPremi() - dibayar)

        // chip filter dipilih → tampilkan ulang daftar
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chipDibayar -> semua.filter { it.status == DataPanen.STATUS_DIBAYAR }
                R.id.chipMenunggu -> semua.filter { it.status == DataPanen.STATUS_MENUNGGU }
                else -> semua
            }
            tampilkanDaftar(filter)
        }
        aturRecyclerView(semua)

        // Pertemuan 9 – Fitur 2: grafik premi per pemanen
        tampilkanGrafik(semua)
    }

    // ================= Pertemuan 9 – Fitur 2: Grafik Premi (MPAndroidChart) =================
    // Sumber: https://github.com/PhilJay/MPAndroidChart (wiki: Setting Data, Formatting Data Values & Axis)

    private fun tampilkanGrafik(daftar: List<DataPanen>) {
        val ctx = requireContext()
        val chart = binding.chartPremi
        val font = ResourcesCompat.getFont(ctx, R.font.inter_regular)

        // 1. Kelompokkan data per pemanen → { "Andi Saputra" = [data1, data2], ... }
        val perPemanen = daftar.groupBy { it.pemanen }
        val namaPemanen = perPemanen.keys.toList()

        // 2. Satu batang per pemanen, ditumpuk: [sudah dibayar, menunggu] dalam ribu rupiah
        val entries = namaPemanen.mapIndexed { i, nama ->
            val dataPemanen = perPemanen.getValue(nama)
            val dibayar = dataPemanen.filter { it.status == DataPanen.STATUS_DIBAYAR }.sumOf { it.premi.total } / 1000f
            val menunggu = dataPemanen.filter { it.status == DataPanen.STATUS_MENUNGGU }.sumOf { it.premi.total } / 1000f
            BarEntry(i.toFloat(), floatArrayOf(dibayar, menunggu))
        }

        // 3. Atur warna & label tiap tumpukan
        val dataSet = BarDataSet(entries, "")
        dataSet.setColors(
            ContextCompat.getColor(ctx, R.color.hijau_sinkron),
            ContextCompat.getColor(ctx, R.color.jingga_brondolan)
        )
        dataSet.stackLabels = arrayOf(getString(R.string.sudah_dibayar), getString(R.string.menunggu))
        dataSet.setDrawValues(false)

        val barData = BarData(dataSet)
        barData.barWidth = 0.55f
        chart.data = barData

        // 4. Tampilan sumbu: X = nama depan pemanen, Y = premi (rb)
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
            typeface = font
            textColor = ContextCompat.getColor(ctx, R.color.teks_sekunder)
            valueFormatter = IndexAxisValueFormatter(namaPemanen.map { it.substringBefore(" ") })
        }
        chart.axisLeft.apply {
            axisMinimum = 0f
            typeface = font
            textColor = ContextCompat.getColor(ctx, R.color.teks_sekunder)
            gridColor = ContextCompat.getColor(ctx, R.color.garis)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String = "${value.toInt()}rb"
            }
        }
        chart.axisRight.isEnabled = false
        chart.legend.apply {
            typeface = font
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            form = Legend.LegendForm.CIRCLE
        }
        chart.description.isEnabled = false
        chart.setScaleEnabled(false)
        chart.setFitBars(true)

        // 5. Animasi batang naik dari bawah, lalu gambar ulang
        chart.animateY(800)
        chart.invalidate()
    }

    // ================= Pertemuan 11 – RecyclerView =================
    // Sumber: modul Pertemuan 11 (RecyclerView) &
    // https://developer.android.com/develop/ui/views/layout/recyclerview

    private fun aturRecyclerView(daftar: List<DataPanen>) {
        // 1. Adapter: data + aksi saat baris diketuk (buat slip PDF)
        adapter = RiwayatAdapter(daftar) { data -> buatSlip(data) }

        binding.rvRiwayat.apply {
            // 2. LayoutManager: daftar vertikal (LinearLayoutManager)
            layoutManager = LinearLayoutManager(requireContext())
            // 3. Garis pemisah antar baris
            addItemDecoration(
                MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
                    dividerColor = ContextCompat.getColor(requireContext(), R.color.garis)
                    isLastItemDecorated = false
                }
            )
            this.adapter = this@RiwayatFragment.adapter
        }
        tampilkanStatusKosong(daftar)
    }

    private fun tampilkanDaftar(daftar: List<DataPanen>) {
        adapter.perbaruiData(daftar)
        tampilkanStatusKosong(daftar)
    }

    private fun tampilkanStatusKosong(daftar: List<DataPanen>) {
        binding.tvKosong.visibility = if (daftar.isEmpty()) View.VISIBLE else View.GONE
    }

    // ================= Pertemuan 9 – Fitur 3: Slip Premi PDF =================

    private fun buatSlip(data: DataPanen) {
        // 1. Slip pembayaran hanya untuk data yang sudah divalidasi (Dibayar)
        if (data.status != DataPanen.STATUS_DIBAYAR) {
            Toast.makeText(requireContext(), R.string.slip_belum_dibayar, Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Buat file PDF (lihat slip/SlipPdf.kt)
        val admin = SessionManager(requireContext()).getUsername()
        val file = SlipPdf.buat(requireContext(), data, admin)

        // 3. Tanya user: buka atau bagikan
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.slip_siap_judul)
            .setMessage(getString(R.string.slip_siap_pesan, file.name))
            .setIcon(R.drawable.ic_picture_as_pdf)
            .setPositiveButton(R.string.slip_bagikan) { _, _ -> SlipPdf.bagikan(requireContext(), file) }
            .setNeutralButton(R.string.slip_buka) { _, _ ->
                try {
                    SlipPdf.buka(requireContext(), file)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), R.string.slip_gagal_buka, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.slip_tutup, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
