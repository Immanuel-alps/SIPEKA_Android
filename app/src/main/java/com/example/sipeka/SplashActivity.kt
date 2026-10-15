package com.example.sipeka

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.sipeka.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TampilanHelper.aturEdgeToEdge(this, binding.root, null)

        // Tunggu 2 detik, lalu tentukan halaman tujuan
        Handler(Looper.getMainLooper()).postDelayed({
            pindahHalaman()
        }, DURASI_SPLASH)
    }

    private fun pindahHalaman() {
        val session = SessionManager(this)

        // isLogin = true  → langsung ke MainActivity
        // isLogin = false → ke LoginActivity
        val tujuan = if (session.isLogin()) {
            MainActivity::class.java
        } else {
            LoginActivity::class.java
        }

        startActivity(Intent(this, tujuan))
        finish() // splash ditutup agar tidak kembali ke sini saat menekan back
    }

    companion object {
        private const val DURASI_SPLASH = 2000L // milidetik
    }
}
