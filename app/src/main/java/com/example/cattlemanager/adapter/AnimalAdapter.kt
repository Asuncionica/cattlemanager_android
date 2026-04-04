package com.example.cattlemanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.databinding.ItemAnimalBinding
import com.example.cattlemanager.model.Animal

class AnimalAdapter(
    private val lista: List<Animal>,
    private val onClick: (Animal) -> Unit
) : RecyclerView.Adapter<AnimalAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAnimalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnimalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animal = lista[position]

        holder.binding.tvNombre.text = "ID: ${animal.identificador}"
        holder.binding.tvRaza.text = "Raza: ${animal.raza}"
        holder.binding.tvSexo.text = "Sexo: ${animal.sexo}"

        holder.itemView.setOnClickListener {
            onClick(animal)
        }
    }

    override fun getItemCount(): Int = lista.size
}