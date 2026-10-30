package com.example.sipeka

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.sipeka.databinding.ActivityEmailGmailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Soal 1: halaman input email Gmail (user hanya mengisi email) */
class EmailGmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailGmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailGmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TampilanHelper.aturEdgeToEdge(this, binding.root, binding.appBar)

        // Toolbar + tombol back
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.judul_email_gmail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnLanjut.setOnClickListener {
            cekEmail()
        }
    }

    private fun cekEmail() {
        // 1. Ambil email (huruf kecil semua supaya GMAIL.COM tetap terbaca)
        val email = binding.etEmail.text.toString().trim().lowercase()

        // 2. Validasi
        if (email.isEmpty()) {
            tampilkanError(getString(R.string.error_email_kosong))
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@gmail.com")) {
            tampilkanError(getString(R.string.error_email_gmail))
            return
        }

        // 3. Valid -> kirim email ke halaman Registrasi lewat Intent
        val intent = Intent(this, RegistrasiActivity::class.java)
        intent.putExtra(EXTRA_EMAIL, email)
        startActivity(intent)
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

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}