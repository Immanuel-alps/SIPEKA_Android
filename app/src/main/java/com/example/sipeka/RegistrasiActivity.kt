package com.example.sipeka

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sipeka.databinding.ActivityRegistrasiBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Soal 2: halaman Registrasi (Nama, Email, Username, Password) */
class RegistrasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrasiBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TampilanHelper.aturEdgeToEdge(this, binding.root, binding.appBar)

        session = SessionManager(this)

        // Toolbar + tombol back
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.judul_registrasi)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Email otomatis terisi dari halaman Register With Gmail & tidak bisa diubah
        val email = intent.getStringExtra(EmailGmailActivity.EXTRA_EMAIL) ?: ""
        binding.etEmail.setText(email)
        binding.tilEmail.isEnabled = false

        binding.btnDaftar.setOnClickListener {
            prosesRegistrasi()
        }
    }

    private fun prosesRegistrasi() {
        // 1. Ambil input
        val nama = binding.etNama.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val username =
            binding.etUsername.text.toString()   // sengaja tidak di-trim agar spasi terdeteksi
        val password = binding.etPassword.text.toString()

        // 2. Validasi
        if (nama.isEmpty() || email.isEmpty() || username.isBlank() || password.isEmpty()) {
            tampilkanError(getString(R.string.error_field_kosong))
            return
        }
        if (username.contains(" ")) {
            tampilkanError(getString(R.string.error_username_spasi))
            return
        }
        if (password.length < 6) {
            tampilkanError(getString(R.string.error_password_minimal))
            return
        }

        // 3. Lolos validasi -> simpan ke SharedPreferences
        session.simpanRegistrasi(nama, email, username, password)

        // 4. Tampilkan halaman informasi "Registrasi Berhasil"
        startActivity(Intent(this, RegistrasiBerhasilActivity::class.java))
        finish()
    }

    private fun tampilkanError(pesan: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.judul_error)
            .setMessage(pesan)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
