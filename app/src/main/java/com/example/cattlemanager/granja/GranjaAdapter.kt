package com.example.cattlemanager.granja

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.databinding.ItemGranjaBinding
import com.example.cattlemanager.model.Granja

class GranjaAdapter(
    private var lista: List<Granja>,
    private val onEditar: (Granja) -> Unit
) : RecyclerView.Adapter<GranjaAdapter.GranjaViewHolder>() {

    inner class GranjaViewHolder(val binding: ItemGranjaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GranjaViewHolder {
        val binding = ItemGranjaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GranjaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GranjaViewHolder, position: Int) {
        val granja = lista[position]

        holder.binding.tvNombreGranja.text = granja.nombre
        holder.binding.tvUbicacion.text = "Ubicación: ${granja.ubicacion}"
        holder.binding.tvTelefono.text = "Teléfono: ${granja.telefono}"

        holder.binding.btnEditarGranja.setOnClickListener {
            onEditar(granja)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Granja>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}