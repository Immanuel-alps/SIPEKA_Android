package com.example.sipeka.validasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sipeka.R
import com.example.sipeka.data.DataPanen
import com.example.sipeka.data.Format
import com.example.sipeka.data.RepositoriPanen
import com.example.sipeka.databinding.FragmentValidasiBinding
import com.example.sipeka.databinding.DialogTandaTanganBinding
import com.example.sipeka.databinding.ItemValidasiBinding
import com.example.sipeka.ttd.PenyimpananTtd
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

            // Fitur tambahan Pertemuan 11: validasi wajib dengan tanda tangan digital pemanen
            item.btnValidasi.setOnClickListener { mintaTandaTangan(data) }
            binding.containerValidasi.addView(item.root)
        }
    }

    // ================= Fitur tambahan Pertemuan 11 – Tanda Tangan Digital Pemanen =================
    // Sesuai SKPL F04 / US04: pembayaran tunai baru sah setelah pemanen tanda tangan di layar HP.

    private fun mintaTandaTangan(data: DataPanen) {
        val dialogBinding = DialogTandaTanganBinding.inflate(layoutInflater)
        dialogBinding.tvInfoTtd.text = getString(
            R.string.ttd_info, data.pemanen, Format.rupiah(data.premi.total)
        )

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.ttd_judul)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.batal, null)
            .setPositiveButton(R.string.ttd_simpan, null) // listener dipasang setelah show()
            .create()

        // 1. Petunjuk hilang & tombol Simpan aktif begitu pemanen mulai tanda tangan
        dialogBinding.tandaTangan.onBerubah = { kosong ->
            dialogBinding.tvPetunjukTtd.visibility = if (kosong) View.VISIBLE else View.GONE
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = !kosong
        }
        dialogBinding.btnUlangi.setOnClickListener { dialogBinding.tandaTangan.hapus() }

        dialog.show()
        val tombolSimpan = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        tombolSimpan.isEnabled = false

        // 2. Simpan → gambar disimpan ke files/ttd, status menjadi Dibayar
        tombolSimpan.setOnClickListener {
            if (dialogBinding.tandaTangan.kosong()) return@setOnClickListener
            val path = PenyimpananTtd.simpan(requireContext(), dialogBinding.tandaTangan.ambilBitmap(), data.id)
            RepositoriPanen.validasi(data.id, path)
            dialog.dismiss()
            Toast.makeText(
                requireContext(),
                getString(R.string.validasi_berhasil, data.pemanen),
                Toast.LENGTH_SHORT
            ).show()
            tampilkanDaftar() // muat ulang daftar
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
