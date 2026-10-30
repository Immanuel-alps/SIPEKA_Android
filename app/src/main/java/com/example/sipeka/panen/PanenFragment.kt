package com.example.sipeka.panen

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.sipeka.BaseActivity
import com.example.sipeka.R
import com.example.sipeka.data.Format
import com.example.sipeka.data.HasilPremi
import com.example.sipeka.data.KalkulatorPremi
import com.example.sipeka.data.RepositoriPanen
import com.example.sipeka.databinding.FragmentPanenBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Halaman Input Panen: form hasil panen + estimasi premi otomatis */
class PanenFragment : Fragment() {

    private var _binding: FragmentPanenBinding? = null
    private val binding get() = _binding!!

    private val lokal = Locale.forLanguageTag("id-ID")
    private val formatTanggal = SimpleDateFormat("dd MMM yyyy", lokal)
    private val kalender = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPanenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.judul_panen)
        }

        aturDropdown()
        aturTanggal()

        // setiap input berubah → hitung ulang estimasi premi
        binding.etBerat.doAfterTextChanged { hitungPremi() }
        binding.etMentah.doAfterTextChanged { hitungPremi() }
        binding.actGrade.setOnItemClickListener { _, _, _, _ -> hitungPremi() }
        binding.toggleLahan.addOnButtonCheckedListener { _, _, _ -> hitungPremi() }

        binding.btnSimpan.setOnClickListener { simpanPanen() }

        hitungPremi()
    }

    private fun aturDropdown() {
        binding.actPemanen.setSimpleItems(RepositoriPanen.daftarPemanen.toTypedArray())
        binding.actBlok.setSimpleItems(RepositoriPanen.daftarBlok.toTypedArray())
        binding.actGrade.setSimpleItems(RepositoriPanen.daftarGrade.toTypedArray())
        // nilai awal
        binding.actBlok.setText(RepositoriPanen.daftarBlok[0], false)
        binding.actGrade.setText(RepositoriPanen.daftarGrade[0], false)
    }

    private fun aturTanggal() {
        binding.etTanggal.setText(formatTanggal.format(kalender.time))
        binding.etTanggal.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, tahun, bulan, hari ->
                    kalender.set(tahun, bulan, hari)
                    binding.etTanggal.setText(formatTanggal.format(kalender.time))
                },
                kalender.get(Calendar.YEAR),
                kalender.get(Calendar.MONTH),
                kalender.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun beratKg(): Int = binding.etBerat.text.toString().toIntOrNull() ?: 0
    private fun janjangMentah(): Int = binding.etMentah.text.toString().toIntOrNull() ?: 0
    private fun grade(): String = binding.actGrade.text.toString().ifEmpty { "C" }
    private fun lahanSulit(): Boolean = binding.toggleLahan.checkedButtonId == R.id.btnSulit

    /** Memanggil KalkulatorPremi lalu menampilkan hasilnya di kartu estimasi */
    private fun hitungPremi(): HasilPremi {
        val hasil = KalkulatorPremi.hitung(beratKg(), lahanSulit(), grade(), janjangMentah())
        val koef = if (lahanSulit()) "1,2" else "1,0"
        binding.tvEstimasi.text = Format.rupiah(hasil.total)
        binding.tvRumus.text = getString(R.string.rumus_premi, koef, grade())
        binding.tvPremiDasar.text = Format.rupiah(hasil.premiDasar)
        binding.tvBonus.text = "+ " + Format.rupiah(hasil.bonusMutu)
        binding.tvDenda.text = "− " + Format.rupiah(hasil.denda)
        return hasil
    }

    private fun simpanPanen() {
        val pemanen = binding.actPemanen.text.toString()
        binding.tilPemanen.error = null
        binding.tilBerat.error = null

        // validasi input wajib
        if (pemanen.isEmpty()) {
            binding.tilPemanen.error = getString(R.string.error_pemanen_kosong)
            return
        }
        if (beratKg() <= 0) {
            binding.tilBerat.error = getString(R.string.error_berat_kosong)
            return
        }

        // simpan ke repositori (status awal: Menunggu validasi)
        val data = RepositoriPanen.tambah(
            pemanen = pemanen,
            blok = binding.actBlok.text.toString(),
            tanggal = binding.etTanggal.text.toString(),
            beratKg = beratKg(),
            grade = grade(),
            lahanSulit = lahanSulit(),
            janjangMentah = janjangMentah()
        )
        Toast.makeText(
            requireContext(),
            getString(R.string.panen_tersimpan, data.pemanen, Format.rupiah(data.premi.total)),
            Toast.LENGTH_LONG
        ).show()

        // setelah tersimpan → buka tab Validasi
        (requireActivity() as BaseActivity).pindahTab(R.id.menu_validasi)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
