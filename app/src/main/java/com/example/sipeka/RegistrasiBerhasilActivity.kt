package com.example.sipeka

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.sipeka.databinding.ActivityRegistrasiBerhasilBinding

/** Halaman informasi setelah registrasi berhasil */
class RegistrasiBerhasilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrasiBerhasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrasiBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TampilanHelper.aturEdgeToEdge(this, binding.root, binding.appBar)

        // Toolbar + tombol back
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.judul_registrasi_berhasil)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Tampilkan data yang baru disimpan di SharedPreferences
        val session = SessionManager(this)
        binding.tvDetail.text = getString(
            R.string.detail_registrasi,
            session.getNamaTerdaftar(),
            session.getEmailTerdaftar(),
            session.getUsernameTerdaftar()
        )

        binding.btnKeLogin.setOnClickListener { kembaliKeLogin() }

        // Tombol back HP juga diarahkan ke Login
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                kembaliKeLogin()
            }
        })
    }

    private fun kembaliKeLogin() {
        // CLEAR_TOP: halaman input email ikut ditutup, kembali ke Login yang sudah ada
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        kembaliKeLogin()
        return true
    }
}
