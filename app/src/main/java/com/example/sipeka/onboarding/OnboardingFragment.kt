package com.example.sipeka.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sipeka.R
import com.example.sipeka.databinding.FragmentOnboardingBinding

/**
 * Pertemuan 11 – satu halaman On Board Screen.
 * Satu class fragment dipakai untuk 3 halaman; isi halaman dipilih lewat argumen posisi
 * (latar, ikon, judul, dan deskripsi diambil dari HalamanOnboarding.SEMUA).
 */
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val posisi = requireArguments().getInt(ARG_POSISI)
        val halaman = HalamanOnboarding.SEMUA[posisi]

        binding.root.setBackgroundResource(halaman.latar)   // background drawable berbeda tiap halaman
        binding.ivIlustrasi.setImageResource(halaman.ikon)
        binding.tvLabel.text = getString(R.string.onboard_nomor, posisi + 1, HalamanOnboarding.SEMUA.size, getString(halaman.label))
        binding.tvJudul.setText(halaman.judul)
        binding.tvDeskripsi.setText(halaman.deskripsi)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_POSISI = "posisi"

        fun newInstance(posisi: Int) = OnboardingFragment().apply {
            arguments = Bundle().apply { putInt(ARG_POSISI, posisi) }
        }
    }
}
