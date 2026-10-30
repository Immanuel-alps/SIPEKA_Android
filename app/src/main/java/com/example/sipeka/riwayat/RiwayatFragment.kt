package com.example.sipeka.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sipeka.R
import com.example.sipeka.data.DataPanen
import com.example.sipeka.data.Format
import com.example.sipeka.data.RepositoriPanen
import com.example.sipeka.databinding.FragmentRiwayatBinding
import com.example.sipeka.databinding.ItemPanenBinding

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
