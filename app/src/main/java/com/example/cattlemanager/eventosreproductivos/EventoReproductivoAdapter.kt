package com.example.cattlemanager.eventosreproductivos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.databinding.ItemEventoReproductivoBinding
import com.example.cattlemanager.model.EventoReproductivo

// Adaptador del RecyclerView de eventos reproductivos; trunca la descripción a 60 caracteres
class EventoReproductivoAdapter(
    private val lista: List<EventoReproductivo>,
    private val onClick: (EventoReproductivo) -> Unit
) : RecyclerView.Adapter<EventoReproductivoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEventoReproductivoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventoReproductivoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = lista[position]

        val descripcionCorta = if (evento.descripcion.length > 60)
            evento.descripcion.take(60) + "..." else evento.descripcion

        holder.binding.tvTipoReproductivo.text = evento.tipo
        holder.binding.tvFechaReproductivo.text = evento.fecha
        holder.binding.tvAnimalReproductivo.text = evento.animal?.identificador ?: "Sin animal"
        holder.binding.tvDescripcionReproductivo.text = descripcionCorta

        holder.binding.btnVerReproductivo.setOnClickListener { onClick(evento) }
    }

    override fun getItemCount(): Int = lista.size
}
