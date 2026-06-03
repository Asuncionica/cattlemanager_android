package com.example.cattlemanager.usuarios

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.databinding.ItemUsuarioBinding
import com.example.cattlemanager.model.Usuario

class UsuarioAdapter(
    private val lista: List<Usuario>,
    private val onClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemUsuarioBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUsuarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = lista[position]

        holder.binding.tvNombreUsuario.text = usuario.nombre
        holder.binding.tvEmailUsuario.text = usuario.email
        holder.binding.tvRolUsuario.text = usuario.rol.nombre

        holder.binding.btnVerUsuario.setOnClickListener {
            onClick(usuario)
        }
    }

    override fun getItemCount(): Int = lista.size
}