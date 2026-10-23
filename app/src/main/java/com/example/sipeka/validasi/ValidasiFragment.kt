package com.example.sipeka.validasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sipeka.R
import com.example.sipeka.data.Format
import com.example.sipeka.data.RepositoriPanen
import com.example.sipeka.databinding.FragmentValidasiBinding
import com.example.sipeka.databinding.ItemValidasiBinding

/** Halaman Validasi: daftar premi yang belum dibayar */
class ValidasiFragment : Fragment() {

    private var _binding: FragmentValidasiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentValidasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.judul_validasi)
        }

        tampilkanDaftar()
    }

    private fun tampilkanDaftar() {
        val daftar = RepositoriPanen.menungguValidasi()
        binding.tvInfoValidasi.text = getString(
            R.string.info_validasi, daftar.size, Format.rupiah(daftar.sumOf { it.premi.total })
        )
        binding.tvKosong.visibility = if (daftar.isEmpty()) View.VISIBLE else View.GONE
        binding.tvInfoValidasi.visibility = if (daftar.isEmpty()) View.GONE else View.VISIBLE

        binding.containerValidasi.removeAllViews()
        daftar.forEach { data ->
            val item = ItemValidasiBinding.inflate(layoutInflater, binding.containerValidasi, false)
            item.tvInisial.text = Format.inisial(data.pemanen)
            item.tvNama.text = data.pemanen
            item.tvDetail.text = "Blok ${data.blok} • ${data.tanggal}" +
                if (data.lahanSulit) " • lahan sulit" else " • lahan datar"
            item.tvBerat.text = Format.kg(data.beratKg)
            item.tvPremiDasar.text = Format.rupiah(data.premi.premiDasar)
            item.tvBonusLabel.text = getString(R.string.bonus_mutu) + " Grade ${data.grade}"
            item.tvBonus.text = "+ " + Format.rupiah(data.premi.bonusMutu)
            item.tvDenda.text = "− " + Format.rupiah(data.premi.denda)
            item.tvTotal.text = Format.rupiah(data.premi.total)

            item.btnValidasi.setOnClickListener {
                RepositoriPanen.validasi(data.id) // status → Dibayar
                Toast.makeText(
                    requireContext(),
                    getString(R.string.validasi_berhasil, data.pemanen),
                    Toast.LENGTH_SHORT
                ).show()
                tampilkanDaftar() // muat ulang daftar
            }
            binding.containerValidasi.addView(item.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
