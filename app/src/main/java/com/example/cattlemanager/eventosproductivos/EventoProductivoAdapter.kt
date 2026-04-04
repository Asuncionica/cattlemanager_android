package com.example.cattlemanager.eventosproductivos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.databinding.ItemEventoProductivoBinding
import com.example.cattlemanager.model.EventoProductivo

class EventoProductivoAdapter(
    private val lista: List<EventoProductivo>,
    private val onClick: (EventoProductivo) -> Unit
) : RecyclerView.Adapter<EventoProductivoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEventoProductivoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventoProductivoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = lista[position]

        val descripcionCorta = if (evento.descripcion.length > 60) {
            evento.descripcion.take(60) + "..."
        } else {
            evento.descripcion
        }

        val identificadorAnimal = evento.animal?.identificador ?: "Sin animal"

        holder.binding.tvTipoEvento.text = evento.tipo
        holder.binding.tvFechaEvento.text = "Fecha: ${evento.fecha}"
        holder.binding.tvAnimalEvento.text = "Animal: $identificadorAnimal"
        holder.binding.tvDescripcionEvento.text = descripcionCorta

        holder.itemView.setOnClickListener {
            onClick(evento)
        }
    }

    override fun getItemCount(): Int = lista.size
}