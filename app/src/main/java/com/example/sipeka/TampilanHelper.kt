package com.example.sipeka

import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Android 15+ menampilkan aplikasi sampai ke balik status bar (edge-to-edge).
 * Fungsi ini menambah padding agar konten tidak tertutup status bar / navigation bar.
 */
object TampilanHelper {

    fun aturEdgeToEdge(activity: ComponentActivity, root: View, bagianAtas: View?) {
        // ikon status bar dibuat putih karena header berwarna maroon
        activity.enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))

        val paddingAtasAwal = bagianAtas?.paddingTop ?: 0
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            if (bagianAtas != null) {
                bagianAtas.setPadding(
                    bagianAtas.paddingLeft, paddingAtasAwal + bars.top,
                    bagianAtas.paddingRight, bagianAtas.paddingBottom
                )
                view.setPadding(bars.left, 0, bars.right, bars.bottom)
            } else {
                view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            }
            insets
        }
    }
}
