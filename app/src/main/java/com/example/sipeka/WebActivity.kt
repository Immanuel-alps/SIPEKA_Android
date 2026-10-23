package com.example.sipeka

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.sipeka.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TampilanHelper.aturEdgeToEdge(this, binding.root, binding.appBar)

        // Ambil data yang dikirim MainActivity lewat Intent
        val url = intent.getStringExtra(EXTRA_URL) ?: getString(R.string.url_web)
        val judul = intent.getStringExtra(EXTRA_JUDUL) ?: getString(R.string.judul_web)

        // Toolbar: judul activity + alamat web + tombol back
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = judul
        supportActionBar?.subtitle = Uri.parse(url).host
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        aturWebView(url)

        // Tombol back HP: mundur di riwayat web dulu, kalau sudah habis baru tutup activity
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun aturWebView(url: String) {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true

        // link di dalam halaman tetap dibuka di WebView (tidak pindah ke browser)
        binding.webView.webViewClient = WebViewClient()

        // update garis progress sesuai persentase halaman yang sudah dimuat
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                binding.progressWeb.progress = newProgress
                binding.progressWeb.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
            }
        }

        binding.webView.loadUrl(url)
    }

    // Tombol back di toolbar → langsung kembali ke MainActivity
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val EXTRA_URL = "extra_url"
        const val EXTRA_JUDUL = "extra_judul"
    }
}
