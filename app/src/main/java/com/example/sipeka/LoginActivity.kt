package com.example.sipeka

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
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

        // Pertemuan 9 – Fitur 1: login sidik jari
        aturLoginSidikJari()

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
            session.simpanUserBiometrik(username)   // Fitur 1: lain kali bisa masuk pakai sidik jari
            masukKeHome(username)
        } else {
            // 5b. Gagal -> tampilkan MaterialAlertDialog
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.judul_login_gagal)
                .setMessage(R.string.error_login_gagal)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
    }

    /** Simpan sesi login lalu buka halaman Home (dipakai login password & sidik jari) */
    private fun masukKeHome(username: String) {
        session.simpanLogin(username)
        Toast.makeText(this, getString(R.string.login_berhasil, username), Toast.LENGTH_SHORT).show()

        val intent = Intent(this, BaseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // ================= Pertemuan 9 – Fitur 1: Login Sidik Jari =================
    // Sumber: https://developer.android.com/training/sign-in/biometric-auth

    private fun aturLoginSidikJari() {
        val username = session.getUserBiometrik()

        // 1. Cek apakah HP punya sensor & sudah ada sidik jari yang terdaftar
        val status = BiometricManager.from(this).canAuthenticate(BIOMETRIC_WEAK)
        val bisaDipakai = status == BiometricManager.BIOMETRIC_SUCCESS && username != null

        // 2. Tombol hanya tampil kalau HP mendukung dan user pernah login pakai password
        binding.btnSidikJari.visibility = if (bisaDipakai) View.VISIBLE else View.GONE
        if (!bisaDipakai || username == null) return
        binding.btnSidikJari.text = getString(R.string.tombol_sidik_jari_user, username)

        // 3. Callback hasil scan sidik jari (dijalankan di main thread)
        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    masukKeHome(username)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // jari tidak cocok, dialog tetap terbuka dan user bisa mencoba lagi
                    Toast.makeText(this@LoginActivity, R.string.biometrik_gagal, Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // tombol "Pakai password" / dibatalkan user → tidak perlu pesan error
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                        errorCode != BiometricPrompt.ERROR_USER_CANCELED
                    ) {
                        Toast.makeText(this@LoginActivity, getString(R.string.biometrik_error, errString), Toast.LENGTH_SHORT).show()
                    }
                }
            })

        // 4. Isi dialog sidik jari
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometrik_judul))
            .setSubtitle(getString(R.string.biometrik_subjudul, username))
            .setDescription(getString(R.string.biometrik_deskripsi))
            .setNegativeButtonText(getString(R.string.biometrik_pakai_password))
            .setAllowedAuthenticators(BIOMETRIC_WEAK)
            .build()

        binding.btnSidikJari.setOnClickListener { prompt.authenticate(info) }
    }

    // Tombol back di toolbar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}