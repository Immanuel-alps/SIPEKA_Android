package com.example.sipeka.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Pertemuan 11 – Adapter ViewPager2 untuk On Board Screen.
 * Menentukan fragment mana yang tampil di setiap halaman yang digeser.
 */
class OnboardingAdapter(
    activity: FragmentActivity,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
