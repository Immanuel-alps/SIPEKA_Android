package com.example.sipeka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sipeka.databinding.ActivityLoginBinding

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
        if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_password_pendek)
            binding.etPassword.requestFocus()
            return
        }

        // 4. Cocokkan dengan akun admin
        if (username == USERNAME_ADMIN && password == PASSWORD_ADMIN) {
            // 5a. Berhasil → simpan isLogin = true ke SharedPreferences
            session.simpanLogin(username)
            Toast.makeText(this, getString(R.string.login_berhasil, username), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            // 5b. Gagal → tampilkan pesan error, isLogin tetap false
            binding.tilPassword.error = getString(R.string.error_login_salah)
            Toast.makeText(this, R.string.error_login_salah, Toast.LENGTH_SHORT).show()
        }
    }

    // Tombol back di toolbar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        // Akun admin sementara (nanti diganti dengan data dari server/API)
        private const val USERNAME_ADMIN = "admin"
        private const val PASSWORD_ADMIN = "sipeka123"
    }
}
