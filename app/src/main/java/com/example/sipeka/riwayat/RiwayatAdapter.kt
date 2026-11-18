package com.example.sipeka.riwayat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sipeka.R
import com.example.sipeka.data.DataPanen
import com.example.sipeka.data.Format
import com.example.sipeka.databinding.ItemPanenBinding

/**
 * Pertemuan 11 – RecyclerView: adapter daftar riwayat premi.
 * Adapter menghubungkan data (List<DataPanen>) dengan tampilan satu baris (item_panen.xml).
 * Baris yang sudah keluar dari layar dipakai ulang (recycle) untuk data berikutnya.
 */
class RiwayatAdapter(
    private var daftar: List<DataPanen>,
    private val onItemClick: (DataPanen) -> Unit
) : RecyclerView.Adapter<RiwayatAdapter.RiwayatViewHolder>() {

    /** ViewHolder menyimpan binding satu baris supaya findViewById tidak dipanggil berulang */
    inner class RiwayatViewHolder(val binding: ItemPanenBinding) :
        RecyclerView.ViewHolder(binding.root)

    // 1. Membuat tampilan baris baru (hanya sebanyak yang muat di layar + sedikit cadangan)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
        val binding = ItemPanenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RiwayatViewHolder(binding)
    }

    // 2. Mengisi baris dengan data pada posisi tertentu
    override fun onBindViewHolder(holder: RiwayatViewHolder, position: Int) {
        val data = daftar[position]
        val context = holder.itemView.context
        with(holder.binding) {
            tvInisial.text = Format.inisial(data.pemanen)
            tvNama.text = data.pemanen
            tvDetail.text = "${data.tanggal} • Blok ${data.blok} • ${Format.kg(data.beratKg)}"
            tvPremi.text = "+" + Format.rupiah(data.premi.total)
            tvStatus.text = data.status
            val warna = if (data.status == DataPanen.STATUS_DIBAYAR) R.color.hijau_sinkron else R.color.jingga_brondolan
            tvStatus.setTextColor(ContextCompat.getColor(context, warna))

            // Pertemuan 9 – Fitur 3: ketuk baris → buat slip PDF (diproses di RiwayatFragment)
            root.setOnClickListener { onItemClick(data) }
        }
    }

    // 3. Jumlah baris yang ditampilkan
    override fun getItemCount(): Int = daftar.size

    /** Dipanggil saat chip filter berubah → ganti isi daftar lalu gambar ulang */
    @SuppressLint("NotifyDataSetChanged")
    fun perbaruiData(daftarBaru: List<DataPanen>) {
        daftar = daftarBaru
        notifyDataSetChanged()
    }
}
