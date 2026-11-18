package com.example.sipeka.onboarding

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.sipeka.BaseActivity
import com.example.sipeka.LoginActivity
import com.example.sipeka.R
import com.example.sipeka.SessionManager
import com.example.sipeka.databinding.ActivityOnboardingBinding

/**
 * Pertemuan 11 – On Board Screen (ViewPager2 + DotsIndicator).
 * Tampil otomatis saat aplikasi pertama kali dibuka, dan bisa dibuka lagi dari menu "Panduan" di Beranda.
 *
 * Sumber:
 * - Modul Pertemuan 11 (Onboard Screen / ViewPager2)
 * - https://developer.android.com/develop/ui/views/animations/screen-slide-2
 * - https://github.com/tommybuonomo/dotsindicator
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var session: SessionManager

    /** true = dibuka dari menu Panduan (sudah login), jadi cukup ditutup saat selesai */
    private val dariMenu by lazy { intent.getBooleanExtra(EXTRA_DARI_MENU, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturInsets()

        session = SessionManager(this)

        // 1. Daftar fragment: 3 halaman, isi diambil dari HalamanOnboarding.SEMUA
        val fragments = HalamanOnboarding.SEMUA.indices.map { OnboardingFragment.newInstance(it) }

        // 2. Pasang adapter ke ViewPager2
        binding.viewPagerOnboarding.adapter = OnboardingAdapter(this, fragments)

        // 3. Hubungkan titik indikator dengan ViewPager2
        binding.dotIndicator.attachTo(binding.viewPagerOnboarding)

        // 4. Teks tombol & tombol Lewati menyesuaikan halaman yang sedang tampil
        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val halamanTerakhir = position == fragments.lastIndex
                binding.btnLanjut.setText(if (halamanTerakhir) R.string.onboard_mulai else R.string.tombol_lanjut)
                binding.btnLewati.visibility = if (halamanTerakhir) View.INVISIBLE else View.VISIBLE
            }
        })

        binding.btnLanjut.setOnClickListener {
            val posisi = binding.viewPagerOnboarding.currentItem
            if (posisi < fragments.lastIndex) {
                binding.viewPagerOnboarding.currentItem = posisi + 1   // geser ke halaman berikutnya
            } else {
                selesai()
            }
        }
        binding.btnLewati.setOnClickListener { selesai() }
    }

    /** Simpan tanda onboarding sudah dilihat, lalu lanjut ke halaman berikutnya */
    private fun selesai() {
        session.simpanOnboardingSelesai()
        if (!dariMenu) {
            val tujuan = if (session.isLogin()) BaseActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this, tujuan))
        }
        finish()
    }

    /** Konten sampai ke balik status bar; hanya bar atas & tombol bawah yang diberi jarak */
    private fun aturInsets() {
        val atasAwal = binding.barAtas.paddingTop
        val bawahAwal = binding.barBawah.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.barAtas.setPadding(
                binding.barAtas.paddingLeft, atasAwal + bars.top,
                binding.barAtas.paddingRight, binding.barAtas.paddingBottom
            )
            binding.barBawah.setPadding(
                binding.barBawah.paddingLeft, binding.barBawah.paddingTop,
                binding.barBawah.paddingRight, bawahAwal + bars.bottom
            )
            insets
        }
    }

    companion object {
        const val EXTRA_DARI_MENU = "extra_dari_menu"
    }
}
