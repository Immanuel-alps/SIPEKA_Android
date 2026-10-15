package com.example.sipeka

import android.content.Context
import android.content.SharedPreferences

/**
 * Menyimpan status login ke SharedPreferences.
 * Data tersimpan di file XML internal aplikasi: sipeka_session.xml
 */
class SessionManager(context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences(NAMA_PREF, Context.MODE_PRIVATE)

    /** Dipanggil saat autentikasi berhasil → isLogin = true */
    fun simpanLogin(username: String) {
        val editor = pref.edit()
        editor.putBoolean(KEY_IS_LOGIN, true)
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }

    /** Membaca status login. Nilai default false (belum pernah login). */
    fun isLogin(): Boolean = pref.getBoolean(KEY_IS_LOGIN, false)

    fun getUsername(): String = pref.getString(KEY_USERNAME, "") ?: ""

    /** Menghapus seluruh data sesi → isLogin kembali false */
    fun logout() {
        pref.edit().clear().apply()
    }

    companion object {
        private const val NAMA_PREF = "sipeka_session"
        private const val KEY_IS_LOGIN = "isLogin"
        private const val KEY_USERNAME = "username"
    }
}
