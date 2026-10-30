package com.example.sipeka.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.sipeka.R
import com.example.sipeka.data.DataPanen
import com.example.sipeka.data.Format
import com.example.sipeka.data.RepositoriPanen
import com.example.sipeka.databinding.FragmentRiwayatBinding
import com.example.sipeka.databinding.ItemPanenBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

/** Halaman Riwayat: rekap semua premi + filter status */
class RiwayatFragment : Fragment() {

    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!

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
        tampilkanDaftar(semua)

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

    private fun tampilkanDaftar(daftar: List<DataPanen>) {
        binding.containerRiwayat.removeAllViews()
        binding.tvKosong.visibility = if (daftar.isEmpty()) View.VISIBLE else View.GONE
        daftar.forEach { data ->
            val item = ItemPanenBinding.inflate(layoutInflater, binding.containerRiwayat, false)
            item.tvInisial.text = Format.inisial(data.pemanen)
            item.tvNama.text = data.pemanen
            item.tvDetail.text = "${data.tanggal} • Blok ${data.blok} • ${Format.kg(data.beratKg)}"
            item.tvPremi.text = "+" + Format.rupiah(data.premi.total)
            item.tvStatus.text = data.status
            val warna = if (data.status == DataPanen.STATUS_DIBAYAR) R.color.hijau_sinkron else R.color.jingga_brondolan
            item.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), warna))
            binding.containerRiwayat.addView(item.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
