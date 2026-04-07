package com.example.cattlemanager.eventosproductivos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.databinding.ItemProduccionBinding
import com.example.cattlemanager.model.EventoProductivo
import java.text.SimpleDateFormat
import java.util.Locale

class ProduccionAdapter : RecyclerView.Adapter<ProduccionAdapter.ProduccionViewHolder>() {

    private val lista = mutableListOf<EventoProductivo>()

    class ProduccionViewHolder(val binding: ItemProduccionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProduccionViewHolder {
        val binding = ItemProduccionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProduccionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProduccionViewHolder, position: Int) {
        val evento = lista[position]
        holder.bind(evento)
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<EventoProductivo>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    private fun ProduccionViewHolder.bind(evento: EventoProductivo) {
        binding.tvAnimal.text = "Animal: ${evento.animal?.identificador ?: "Sin animal"}"
        binding.tvTipo.text = "Tipo: ${evento.tipo ?: "Sin tipo"}"
        binding.tvFecha.text = "Fecha: ${formatearFecha(evento.fecha)}"
    }

    private fun formatearFecha(fecha: Any?): String {
        return when (fecha) {
            null -> "Sin fecha"
            is java.util.Date -> {
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formato.format(fecha)
            }
            else -> fecha.toString()
        }
    }
}