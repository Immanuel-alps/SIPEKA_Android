package com.example.sipeka

import android.content.Context
import android.content.SharedPreferences

/**
 * - sipeka_session.xml : status login (isLogin, username)
 * - sipeka_akun.xml    : data registrasi (nama, email, username, password)
 * Data akun dipisah supaya TIDAK ikut terhapus saat logout.
 */
class SessionManager(context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences(NAMA_PREF, Context.MODE_PRIVATE)

    private val prefAkun: SharedPreferences =
        context.getSharedPreferences(NAMA_PREF_AKUN, Context.MODE_PRIVATE)

    // ================= SESI LOGIN =================
    fun simpanLogin(username: String) {
        val editor = pref.edit()
        editor.putBoolean(KEY_IS_LOGIN, true)
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }

    fun isLogin(): Boolean = pref.getBoolean(KEY_IS_LOGIN, false)

    fun getUsername(): String = pref.getString(KEY_USERNAME, "") ?: ""

    fun logout() {
        pref.edit().clear().apply()
    }

    // ================= DATA REGISTRASI =================
    fun simpanRegistrasi(nama: String, email: String, username: String, password: String) {
        val editor = prefAkun.edit()
        editor.putString(KEY_NAMA, nama)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_REG_USERNAME, username)
        editor.putString(KEY_REG_PASSWORD, password)
        editor.apply()
    }

    fun getNamaTerdaftar(): String = prefAkun.getString(KEY_NAMA, "") ?: ""
    fun getEmailTerdaftar(): String = prefAkun.getString(KEY_EMAIL, "") ?: ""
    fun getUsernameTerdaftar(): String = prefAkun.getString(KEY_REG_USERNAME, "") ?: ""

    /** true jika username & password sama dengan data registrasi yang tersimpan */
    fun cocokDenganAkunTerdaftar(username: String, password: String): Boolean {
        val usernameTersimpan = prefAkun.getString(KEY_REG_USERNAME, null)
        val passwordTersimpan = prefAkun.getString(KEY_REG_PASSWORD, null)

        if (usernameTersimpan == null || passwordTersimpan == null) return false
        return username == usernameTersimpan && password == passwordTersimpan
    }

    // ================= LOGIN SIDIK JARI =================

    /** Username terakhir yang berhasil login pakai password → dipakai untuk login sidik jari */
    fun simpanUserBiometrik(username: String) {
        prefAkun.edit().putString(KEY_USER_BIOMETRIK, username).apply()
    }

    /** null = belum pernah login pakai password, jadi login sidik jari belum bisa dipakai */
    fun getUserBiometrik(): String? = prefAkun.getString(KEY_USER_BIOMETRIK, null)

    // ================= ON BOARD SCREEN (Pertemuan 11) =================

    /** Disimpan di sipeka_akun supaya onboarding tidak muncul lagi walau user logout */
    fun simpanOnboardingSelesai() {
        prefAkun.edit().putBoolean(KEY_ONBOARDING_SELESAI, true).apply()
    }

    fun sudahLihatOnboarding(): Boolean = prefAkun.getBoolean(KEY_ONBOARDING_SELESAI, false)

    companion object {
        private const val NAMA_PREF = "sipeka_session"
        private const val KEY_IS_LOGIN = "isLogin"
        private const val KEY_USERNAME = "username"

        private const val NAMA_PREF_AKUN = "sipeka_akun"
        private const val KEY_NAMA = "nama"
        private const val KEY_EMAIL = "email"
        private const val KEY_REG_USERNAME = "reg_username"
        private const val KEY_REG_PASSWORD = "reg_password"
        private const val KEY_USER_BIOMETRIK = "user_biometrik"
        private const val KEY_ONBOARDING_SELESAI = "onboarding_selesai"
    }
}