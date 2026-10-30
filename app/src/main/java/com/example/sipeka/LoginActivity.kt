package com.example.sipeka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sipeka.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TampilanHelper.aturEdgeToEdge(this, binding.root, binding.appBar)

        session = SessionManager(this)

        // Toolbar + tombol back
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.judul_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnMasuk.setOnClickListener {
            prosesLogin()
        }

        // Soal 1: tombol Register With Gmail -> halaman input email
        binding.btnRegisterGmail.setOnClickListener {
            startActivity(Intent(this, EmailGmailActivity::class.java))
        }
    }

    private fun prosesLogin() {
        // 1. Ambil input dari form
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // 2. Hapus pesan error sebelumnya
        binding.tilUsername.error = null
        binding.tilPassword.error = null

        // 3. Validasi: input tidak boleh kosong
        if (username.isEmpty()) {
            binding.tilUsername.error = getString(R.string.error_username_kosong)
            binding.etUsername.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_password_kosong)
            binding.etPassword.requestFocus()
            return
        }

        // 4. Boleh masuk jika memenuhi SALAH SATU kondisi
        val kondisi1 = username == password                                 // username == password
        val kondisi2 = session.cocokDenganAkunTerdaftar(username, password)  // data SharedPreferences

        if (kondisi1 || kondisi2) {
            // 5a. Berhasil -> simpan isLogin = true lalu ke halaman Home
            session.simpanLogin(username)
            Toast.makeText(this, getString(R.string.login_berhasil, username), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, BaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            // 5b. Gagal -> tampilkan MaterialAlertDialog
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.judul_login_gagal)
                .setMessage(R.string.error_login_gagal)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
    }

    // Tombol back di toolbar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}