package com.example.cattlemanager.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanager.R
import com.example.cattlemanager.databinding.ItemAnimalBinding
import com.example.cattlemanager.model.Animal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AnimalAdapter(
    private val lista: List<Animal>,
    private val onClick: (Animal) -> Unit
) : RecyclerView.Adapter<AnimalAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAnimalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnimalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animal = lista[position]

        holder.binding.tvNombre.text = animal.identificador
        holder.binding.tvRazaEdad.text = "${animal.raza} · ${calcularEdad(animal.fechaNacimiento)}"

        val esMacho = animal.sexo.equals("Macho", ignoreCase = true)
        val colorBarra = if (esMacho) Color.parseColor("#1565C0") else Color.parseColor("#2E7D32")
        val fondoRes = if (esMacho) R.drawable.animal_card_male else R.drawable.animal_card_female

        holder.binding.layoutFondo.setBackgroundResource(fondoRes)
        holder.binding.viewSexoBarra.setBackgroundColor(colorBarra)
        holder.binding.tvSexoBadge.text = if (esMacho) "♂" else "♀"

        holder.binding.root.setOnClickListener { onClick(animal) }
    }

    override fun getItemCount(): Int = lista.size

    private fun calcularEdad(fechaNacimiento: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fecha = sdf.parse(fechaNacimiento) ?: return ""
            val hoy = Calendar.getInstance()
            val nac = Calendar.getInstance().apply { time = fecha }
            val meses = (hoy.get(Calendar.YEAR) - nac.get(Calendar.YEAR)) * 12 +
                        (hoy.get(Calendar.MONTH) - nac.get(Calendar.MONTH))
            when {
                meses < 1  -> "< 1 mes"
                meses < 12 -> "$meses ${if (meses == 1) "mes" else "meses"}"
                else       -> { val a = meses / 12; "$a ${if (a == 1) "año" else "años"}" }
            }
        } catch (e: Exception) { "" }
    }
}
