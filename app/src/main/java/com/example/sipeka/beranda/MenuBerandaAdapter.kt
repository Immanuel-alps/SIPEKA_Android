package com.example.sipeka.beranda

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sipeka.R
import com.example.sipeka.databinding.ItemMenuBerandaBinding

/**
 * Pertemuan 11 – Adapter grid "Menu cepat" di Beranda.
 * Ditampilkan dengan RecyclerView + GridLayoutManager (3 kolom).
 */
class MenuBerandaAdapter(
    private val daftarMenu: List<MenuBeranda>,
    private val onMenuClick: (MenuBeranda) -> Unit
) : RecyclerView.Adapter<MenuBerandaAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(val binding: ItemMenuBerandaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBerandaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = daftarMenu[position]
        val context = holder.itemView.context
        with(holder.binding) {
            tvJudulMenu.setText(menu.judul)
            ivIkon.setImageResource(menu.ikon)
            // warna kotak ikon berbeda tiap menu, ikon selalu putih
            ivIkon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, menu.warna))
            ivIkon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.putih_susu))
            kotakMenu.contentDescription = context.getString(menu.judul)
            kotakMenu.setOnClickListener { onMenuClick(menu) }
        }
    }

    override fun getItemCount(): Int = daftarMenu.size
}
