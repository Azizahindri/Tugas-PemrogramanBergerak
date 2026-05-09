package com.example.patientapp.ui.pasien

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.patientapp.databinding.ItemPasienBinding
import com.example.patientapp.model.Pasien

class PasienAdapter : ListAdapter<Pasien, PasienAdapter.PasienViewHolder>(PasienDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasienViewHolder {
        val binding = ItemPasienBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PasienViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PasienViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    inner class PasienViewHolder(private val binding: ItemPasienBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pasien: Pasien, nomor: Int) {
            binding.apply {
                tvNomor.text = nomor.toString()
                tvNamaPasien.text = pasien.nama
                tvTanggalLahir.text = formatTanggal(pasien.tanggalLahir)
                tvJenisKelamin.text = formatJenisKelamin(pasien.jenisKelamin)
                tvAlamat.text = pasien.alamat
                tvNoTelepon.text = pasien.noTelepon

                // Warna badge jenis kelamin
                val badgeColor = if (pasien.jenisKelamin == "L") {
                    root.context.getColor(android.R.color.holo_blue_light)
                } else {
                    root.context.getColor(android.R.color.holo_red_light)
                }
                tvJenisKelamin.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(badgeColor)
            }
        }

        private fun formatTanggal(tanggal: String): String {
            return try {
                val parts = tanggal.split("-")
                val bulan = listOf(
                    "", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
                )
                "${parts[2]} ${bulan[parts[1].toInt()]} ${parts[0]}"
            } catch (e: Exception) {
                tanggal
            }
        }

        private fun formatJenisKelamin(kode: String): String {
            return when (kode.uppercase()) {
                "L" -> "Laki-laki"
                "P" -> "Perempuan"
                else -> kode
            }
        }
    }

    class PasienDiffCallback : DiffUtil.ItemCallback<Pasien>() {
        override fun areItemsTheSame(oldItem: Pasien, newItem: Pasien) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Pasien, newItem: Pasien) = oldItem == newItem
    }
}
