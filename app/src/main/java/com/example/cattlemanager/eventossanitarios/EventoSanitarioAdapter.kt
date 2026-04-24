package com.example.cattlemanager.eventossanitarios

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.databinding.ItemEventoSanitarioBinding
import com.example.cattlemanager.model.EventoSanitario

// Adaptador del RecyclerView de eventos sanitarios; trunca la descripción a 60 caracteres
class EventoSanitarioAdapter(
    private val lista: List<EventoSanitario>,
    private val onClick: (EventoSanitario) -> Unit
) : RecyclerView.Adapter<EventoSanitarioAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEventoSanitarioBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventoSanitarioBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = lista[position]

        val descripcionCorta = if (evento.descripcion.length > 60)
            evento.descripcion.take(60) + "..." else evento.descripcion

        holder.binding.tvTipoSanitario.text = evento.tipo
        holder.binding.tvFechaSanitario.text = evento.fecha
        holder.binding.tvAnimalSanitario.text = evento.animal?.identificador ?: "Sin animal"
        holder.binding.tvDescripcionSanitario.text = descripcionCorta

        holder.binding.btnVerSanitario.setOnClickListener { onClick(evento) }
    }

    override fun getItemCount(): Int = lista.size
}
