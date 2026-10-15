package com.example.sipeka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sipeka.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Halaman Awal: tidak memakai toolbar & tombol back */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TampilanHelper.aturEdgeToEdge(this, binding.root, binding.header)

        session = SessionManager(this)

        tampilkanHeader()

        // Tombol ke halaman web
        binding.menuWeb.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(WebActivity.EXTRA_URL, getString(R.string.url_web))
            intent.putExtra(WebActivity.EXTRA_JUDUL, getString(R.string.judul_web))
            startActivity(intent)
        }

        // Tombol ke halaman profil developer
        binding.menuProfil.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            konfirmasiLogout()
        }
    }

    private fun tampilkanHeader() {
        val format = SimpleDateFormat("EEEE • dd MMM yyyy", Locale.forLanguageTag("id-ID"))
        binding.tvTanggal.text = format.format(Date()).uppercase(Locale.forLanguageTag("id-ID"))
        binding.tvSapaan.text = getString(R.string.sapaan, session.getUsername())
    }

    private fun konfirmasiLogout() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.keluar)
            .setMessage(R.string.konfirmasi_keluar)
            .setNegativeButton(R.string.batal, null)
            .setPositiveButton(R.string.ya_keluar) { _, _ ->
                session.logout() // isLogin kembali false
                Toast.makeText(this, R.string.berhasil_keluar, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .show()
    }
}
