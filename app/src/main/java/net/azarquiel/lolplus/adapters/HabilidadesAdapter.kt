package net.azarquiel.lolplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_habilidades.view.*
import net.azarquiel.lolplus.model.Habilidad

class HabilidadesAdapter(val context: Context,
                         val layout: Int
) : RecyclerView.Adapter<HabilidadesAdapter.ViewHolder>() {

    private var dataList: List<Habilidad> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setHabilidades(habilidades: List<Habilidad>) {
        this.dataList = habilidades
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Habilidad){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem
            itemView.tvtecla.text = dataItem.tecla
            itemView.tvnombrehabilidad.text = dataItem.nombre
            itemView.tvdescripcion.text = dataItem.descripcion

            Picasso.get().load(dataItem.imagen).into( itemView.ivhabilidad)
            itemView.tag = dataItem
        }

    }
}