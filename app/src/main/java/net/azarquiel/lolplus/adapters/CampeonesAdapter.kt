package net.azarquiel.lolplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_campeones.view.*
import net.azarquiel.lolplus.model.Campeon

class CampeonesAdapter(val context: Context,
                       val layout: Int
) : RecyclerView.Adapter<CampeonesAdapter.ViewHolder>() {

    private var dataList: List<Campeon> = emptyList()

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

    internal fun setCampeones(campeones: List<Campeon>) {
        this.dataList = campeones
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Campeon){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem
            itemView.nombrerow.text = dataItem.nombre
            itemView.rolrow.text = dataItem.rol
            if(dataItem.dificultad=="1"){
                itemView.rowdificultad.text="Baja"
            }else if(dataItem.dificultad=="2"){
                itemView.rowdificultad.text="Moderada"
            }else{
                itemView.rowdificultad.text="Alta"
            }


            Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${dataItem.nombre}.png?image=q_auto,w_140&v=1585730185").into( itemView.ivrow)
            itemView.tag = dataItem
        }

    }
}