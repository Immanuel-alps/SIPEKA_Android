package com.example.sipeka.lainnya

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sipeka.LoginActivity
import com.example.sipeka.ProfileActivity
import com.example.sipeka.R
import com.example.sipeka.SessionManager
import com.example.sipeka.WebActivity
import com.example.sipeka.databinding.FragmentLainnyaBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Halaman Lainnya: info akun, profil developer, halaman web, logout */
class LainnyaFragment : Fragment() {

    private var _binding: FragmentLainnyaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLainnyaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.judul_lainnya)
        }

        val session = SessionManager(requireContext())
        binding.tvUsername.text = session.getUsername()

        // Intent dari fragment memakai requireContext(), bukan this
        binding.rowProfil.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
        binding.rowWeb.setOnClickListener {
            val intent = Intent(requireContext(), WebActivity::class.java)
            intent.putExtra(WebActivity.EXTRA_URL, getString(R.string.url_web))
            intent.putExtra(WebActivity.EXTRA_JUDUL, getString(R.string.judul_web))
            startActivity(intent)
        }
        binding.rowKeluar.setOnClickListener { konfirmasiLogout(session) }
    }

    private fun konfirmasiLogout(session: SessionManager) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.keluar)
            .setMessage(R.string.konfirmasi_keluar)
            .setNegativeButton(R.string.batal, null)
            .setPositiveButton(R.string.ya_keluar) { _, _ ->
                session.logout() // isLogin kembali false
                Toast.makeText(requireContext(), R.string.berhasil_keluar, Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish() // tutup BaseActivity
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
