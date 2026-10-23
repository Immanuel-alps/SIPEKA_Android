package com.example.sipeka.beranda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sipeka.BaseActivity
import com.example.sipeka.R
import com.example.sipeka.SessionManager
import com.example.sipeka.data.DataPanen
import com.example.sipeka.data.Format
import com.example.sipeka.data.RepositoriPanen
import com.example.sipeka.databinding.FragmentBerandaBinding
import com.example.sipeka.databinding.ItemPanenBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar di fragment harus lewat (requireActivity() as AppCompatActivity)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.judul_beranda)
        }

        tampilkanHeader()
        tampilkanRingkasan()
        tampilkanAktivitas()

        // kartu "pembayaran menunggu" & "Lihat semua" → pindah tab
        binding.cardMenunggu.setOnClickListener {
            (requireActivity() as BaseActivity).pindahTab(R.id.menu_validasi)
        }
        binding.tvLihatSemua.setOnClickListener {
            (requireActivity() as BaseActivity).pindahTab(R.id.menu_riwayat)
        }
    }

    private fun tampilkanHeader() {
        val lokal = Locale.forLanguageTag("id-ID")
        val format = SimpleDateFormat("EEEE • dd MMM yyyy", lokal)
        binding.tvTanggal.text = format.format(Date()).uppercase(lokal)

        // requireContext() dipakai karena fragment bukan Context
        val session = SessionManager(requireContext())
        binding.tvSapaan.text = getString(R.string.sapaan, session.getUsername())
    }

    private fun tampilkanRingkasan() {
        val menunggu = RepositoriPanen.menungguValidasi()
        binding.tvTotalPremi.text = Format.rupiah(RepositoriPanen.totalPremi())
        binding.tvJumlahPemanen.text = RepositoriPanen.jumlahPemanenAktif().toString()
        binding.tvJumlahValidasi.text = menunggu.size.toString()
        binding.tvMenunggu.text = getString(R.string.pembayaran_menunggu, menunggu.size)
        binding.tvNilaiMenunggu.text =
            getString(R.string.nilai_menunggu, Format.rupiah(menunggu.sumOf { it.premi.total }))
    }

    /** Menampilkan 3 data panen terbaru memakai layout item_panen.xml */
    private fun tampilkanAktivitas() {
        binding.containerAktivitas.removeAllViews()
        RepositoriPanen.semua().take(3).forEach { data ->
            val item = ItemPanenBinding.inflate(layoutInflater, binding.containerAktivitas, false)
            item.tvInisial.text = Format.inisial(data.pemanen)
            item.tvNama.text = data.pemanen
            item.tvDetail.text = "Blok ${data.blok} • ${Format.kg(data.beratKg)} • ${data.tanggal}"
            item.tvPremi.text = Format.rupiah(data.premi.total)
            item.tvStatus.text = data.status
            val warna = if (data.status == DataPanen.STATUS_DIBAYAR) R.color.hijau_sinkron else R.color.jingga_brondolan
            item.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), warna))
            binding.containerAktivitas.addView(item.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // mencegah memory leak
    }
}
