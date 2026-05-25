package com.example.cattlemanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.databinding.ItemLoteGeneticoBinding
import com.example.cattlemanager.model.LoteGeneticoResponse

class LoteGeneticoAdapter(
    private var lotes: List<LoteGeneticoResponse>,
    private val onEliminar: (LoteGeneticoResponse) -> Unit
) : RecyclerView.Adapter<LoteGeneticoAdapter.LoteViewHolder>() {

    fun actualizar(nuevosLotes: List<LoteGeneticoResponse>) {
        lotes = nuevosLotes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoteViewHolder {
        val binding = ItemLoteGeneticoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoteViewHolder, position: Int) {
        holder.bind(lotes[position], onEliminar)
    }

    override fun getItemCount(): Int = lotes.size

    class LoteViewHolder(
        private val binding: ItemLoteGeneticoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lote: LoteGeneticoResponse, onEliminar: (LoteGeneticoResponse) -> Unit) {
            binding.tvNombreLote.text = lote.nombre
            binding.tvDescripcionLote.text = lote.descripcion?.takeIf { it.isNotBlank() }
                ?: "Sin descripción"
            binding.tvVariedadLote.text = lote.variedad?.takeIf { it.isNotBlank() }
                ?: "Variedad no indicada"
            binding.btnEliminarLote.setOnClickListener { onEliminar(lote) }
        }
    }
}
