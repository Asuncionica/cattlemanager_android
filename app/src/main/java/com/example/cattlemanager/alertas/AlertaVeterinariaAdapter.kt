package com.example.cattlemanager.alertas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.databinding.ItemAlertaVeterinariaBinding
import com.example.cattlemanager.model.AlertaVeterinaria

class AlertaVeterinariaAdapter(
    private val lista: List<AlertaVeterinaria>,
    private val onAtender: (AlertaVeterinaria) -> Unit
) : RecyclerView.Adapter<AlertaVeterinariaAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAlertaVeterinariaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlertaVeterinariaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alerta = lista[position]

        holder.binding.tvAnimalAlerta.text = alerta.animal?.identificador ?: "Sin animal"
        holder.binding.tvDescripcionAlerta.text = alerta.descripcion
        holder.binding.tvMetaAlerta.text = "Por: ${alerta.creadoPor?.nombre ?: "?"} · ${alerta.fecha}"

        if (alerta.atendida) {
            // Alerta ya atendida: barra verde, sin botón
            holder.binding.viewEstado.setBackgroundColor(Color.parseColor("#388E3C"))
            holder.binding.btnAtender.visibility = View.GONE
            holder.binding.root.alpha = 0.6f
        } else {
            // Alerta pendiente: barra naranja, botón visible
            holder.binding.viewEstado.setBackgroundColor(Color.parseColor("#FF6F00"))
            holder.binding.btnAtender.visibility = View.VISIBLE
            holder.binding.root.alpha = 1.0f
            holder.binding.btnAtender.setOnClickListener { onAtender(alerta) }
        }
    }

    override fun getItemCount(): Int = lista.size
}
