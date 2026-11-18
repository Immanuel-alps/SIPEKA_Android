package com.example.sipeka.ttd

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.sipeka.R
import kotlin.math.max
import kotlin.math.min

/**
 * Fitur tambahan Pertemuan 11 – Tanda Tangan Digital Pemanen (F04 di SKPL).
 * Custom View sederhana: jari digeser di layar → garis digambar mengikuti sentuhan.
 *
 * Sumber:
 * - Custom drawing (Android Developers): https://developer.android.com/develop/ui/views/layout/custom-views/custom-drawing
 * - Touch gestures: https://developer.android.com/develop/ui/views/touch-and-input/gestures/detector
 */
class TandaTanganView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val path = Path()
    private var xTerakhir = 0f
    private var yTerakhir = 0f

    /** dipanggil saat user mulai/selesai menggambar → dipakai untuk mengaktifkan tombol Simpan */
    var onBerubah: ((kosong: Boolean) -> Unit)? = null

    private val kuas = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.maroon_tua)
        style = Paint.Style.STROKE
        strokeWidth = 4f * resources.displayMetrics.density
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    fun kosong(): Boolean = path.isEmpty

    fun hapus() {
        path.reset()
        invalidate()
        onBerubah?.invoke(true)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, kuas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // jangan sampai dialog/scroll ikut tergeser saat menulis
                parent?.requestDisallowInterceptTouchEvent(true)
                path.moveTo(x, y)
                path.lineTo(x + 0.1f, y + 0.1f) // supaya titik (tap) juga terlihat
            }
            MotionEvent.ACTION_MOVE -> {
                // garis dihaluskan dengan kurva quadTo di antara titik sentuhan
                path.quadTo(xTerakhir, yTerakhir, (x + xTerakhir) / 2, (y + yTerakhir) / 2)
            }
            MotionEvent.ACTION_UP -> {
                path.lineTo(x, y)
                onBerubah?.invoke(false)
            }
            else -> return false
        }
        xTerakhir = x
        yTerakhir = y
        invalidate()
        return true
    }

    /** Hasil tanda tangan sebagai Bitmap berlatar transparan, dipotong pas di area coretan */
    fun ambilBitmap(): Bitmap {
        val batas = RectF()
        path.computeBounds(batas, true)
        val jarak = kuas.strokeWidth * 2
        val kiri = max(0f, batas.left - jarak)
        val atas = max(0f, batas.top - jarak)
        val kanan = min(width.toFloat(), batas.right + jarak)
        val bawah = min(height.toFloat(), batas.bottom + jarak)

        val bitmap = Bitmap.createBitmap(
            max(1, (kanan - kiri).toInt()), max(1, (bawah - atas).toInt()), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.translate(-kiri, -atas)
        canvas.drawPath(path, kuas)
        return bitmap
    }
}
