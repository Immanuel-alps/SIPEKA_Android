package com.example.sipeka

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.sipeka.beranda.BerandaFragment
import com.example.sipeka.databinding.ActivityBaseBinding
import com.example.sipeka.lainnya.LainnyaFragment
import com.example.sipeka.panen.PanenFragment
import com.example.sipeka.riwayat.RiwayatFragment
import com.example.sipeka.validasi.ValidasiFragment

/** Activity utama setelah login: wadah fragment + Bottom Navigation 5 menu */
class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // padding bawah = 0, karena BottomNavigationView sudah mengatur jarak navigation bar sendiri
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // BerandaFragment sebagai fragment default (hanya saat pertama kali dibuka)
        if (savedInstanceState == null) {
            replaceFragment(BerandaFragment())
        }

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_beranda -> {
                    replaceFragment(BerandaFragment())
                    true
                }
                R.id.menu_panen -> {
                    replaceFragment(PanenFragment())
                    true
                }
                R.id.menu_validasi -> {
                    replaceFragment(ValidasiFragment())
                    true
                }
                R.id.menu_riwayat -> {
                    replaceFragment(RiwayatFragment())
                    true
                }
                R.id.menu_lainnya -> {
                    replaceFragment(LainnyaFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            //.addToBackStack(null) -> dinonaktifkan agar saat back langsung keluar aplikasi
            .commit()
    }

    /** Dipanggil dari fragment untuk pindah tab, contoh: Beranda → Validasi */
    fun pindahTab(menuId: Int) {
        binding.bottomNavView.selectedItemId = menuId
    }
}
