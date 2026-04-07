package com.example.cattlemanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.databinding.ItemAnimalBinding
import com.example.cattlemanager.model.Animal

// Adapter para mostrar una lista de animales en un RecyclerView
class AnimalAdapter(

    // Lista de animales que se van a mostrar
    private val lista: List<Animal>,

    // Función lambda que se ejecuta cuando se hace click en un animal
    private val onClick: (Animal) -> Unit

) : RecyclerView.Adapter<AnimalAdapter.ViewHolder>() {

    // ViewHolder: representa cada fila de la lista
    class ViewHolder(val binding: ItemAnimalBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Se ejecuta cuando se necesita crear una nueva fila (vista)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Infla el layout XML de cada item (ItemAnimal)
        val binding = ItemAnimalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    // Se ejecuta para asignar datos a cada fila
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Obtiene el animal en la posición actual
        val animal = lista[position]

        // Asigna los datos del animal a las vistas del layout
        holder.binding.tvNombre.text = "ID: ${animal.identificador}"
        holder.binding.tvRaza.text = "Raza: ${animal.raza}"
        holder.binding.tvSexo.text = "Sexo: ${animal.sexo}"

        // Define qué ocurre al pulsar un elemento de la lista
        holder.itemView.setOnClickListener {
            onClick(animal)
        }
    }

    // Devuelve el número total de elementos en la lista
    override fun getItemCount(): Int = lista.size
}