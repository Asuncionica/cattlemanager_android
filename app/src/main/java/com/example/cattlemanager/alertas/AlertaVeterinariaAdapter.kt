package com.example.cattlemanager.alertas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.R
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

        val ctx = holder.itemView.context
        if (alerta.atendida) {
            holder.binding.layoutAlertaFondo.setBackgroundResource(R.drawable.alerta_item_done)
            holder.binding.viewEstado.setBackgroundColor(ContextCompat.getColor(ctx, R.color.status_done))
            holder.binding.btnAtender.visibility = View.GONE
        } else {
            holder.binding.layoutAlertaFondo.setBackgroundResource(R.drawable.alerta_item_pending)
            holder.binding.viewEstado.setBackgroundColor(ContextCompat.getColor(ctx, R.color.status_pending))
            holder.binding.btnAtender.visibility = View.VISIBLE
            holder.binding.btnAtender.setOnClickListener { onAtender(alerta) }
        }
    }

    override fun getItemCount(): Int = lista.size
}
