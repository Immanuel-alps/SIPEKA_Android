package com.example.sipeka

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sipeka.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TampilanHelper.aturEdgeToEdge(this, binding.root, binding.appBar)

        // Toolbar + tombol back
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.judul_profil)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Klik sosial media → buka di browser / aplikasi terkait
        binding.rowInstagram.setOnClickListener { bukaLink(getString(R.string.link_instagram)) }
        binding.rowGithub.setOnClickListener { bukaLink(getString(R.string.link_github)) }
        binding.rowLinkedin.setOnClickListener { bukaLink(getString(R.string.link_linkedin)) }
        binding.rowEmail.setOnClickListener { bukaLink("mailto:" + getString(R.string.dev_email)) }
    }

    private fun bukaLink(link: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.gagal_buka_link, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
