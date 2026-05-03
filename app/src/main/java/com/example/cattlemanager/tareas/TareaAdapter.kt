package com.example.cattlemanager.tareas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.R
import com.example.cattlemanager.databinding.ItemTareaBinding
import com.example.cattlemanager.model.Tarea

class TareaAdapter(
    private val lista: List<Tarea>,
    private val onClick: (Tarea) -> Unit
) : RecyclerView.Adapter<TareaAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTareaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTareaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tarea = lista[position]
        val ctx = holder.itemView.context

        val descripcionCorta = if (tarea.descripcion.length > 80) {
            tarea.descripcion.take(80) + "..."
        } else {
            tarea.descripcion
        }

        holder.binding.tvTituloTarea.text = tarea.titulo
        holder.binding.tvDescripcionTarea.text = descripcionCorta
        holder.binding.tvFechaTarea.text = "Vence: ${tarea.fechaVencimiento}"
        holder.binding.tvPeonTarea.text = tarea.peon?.nombre ?: ""

        if (tarea.completada) {
            holder.binding.layoutTareaFondo.setBackgroundResource(R.drawable.task_item_done)
            holder.binding.viewEstadoBarra.setBackgroundColor(ContextCompat.getColor(ctx, R.color.status_done))
            holder.binding.tvEstadoTarea.text = "✓ Completada"
            holder.binding.tvEstadoTarea.setTextColor(ContextCompat.getColor(ctx, R.color.status_done))
            holder.binding.tvEstadoTarea.setBackgroundColor(0x00000000)
        } else {
            holder.binding.layoutTareaFondo.setBackgroundResource(R.drawable.task_item_pending)
            holder.binding.viewEstadoBarra.setBackgroundColor(ContextCompat.getColor(ctx, R.color.status_pending))
            holder.binding.tvEstadoTarea.text = "Pendiente"
            holder.binding.tvEstadoTarea.setTextColor(ContextCompat.getColor(ctx, R.color.status_pending))
            holder.binding.tvEstadoTarea.setBackgroundColor(0x00000000)
        }

        holder.itemView.setOnClickListener { onClick(tarea) }
    }

    override fun getItemCount(): Int = lista.size
}
