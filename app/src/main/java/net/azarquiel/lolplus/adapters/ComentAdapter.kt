package net.azarquiel.lolplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_comentario.view.*
import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.model.Comentario

class ComentAdapter(val context: Context,
                    val layout: Int,
                    val listener: OnLongClickListenerComent
) : RecyclerView.Adapter<ComentAdapter.ViewHolder>() {

    private var dataList: List<Comentario> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item,listener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setComentarios(comentarios: List<Comentario>) {
        this.dataList = comentarios
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Comentario,listener: OnLongClickListenerComent){
            itemView.tvcomentario.text = dataItem.comentario
            itemView.tvfechacomentario.text=dataItem.fecha
            itemView.tvnickcomentario.text=dataItem.usuario.nick

            if(dataItem.usuario.champfav=="noone"){
                itemView.ivcomentario.setImageResource(R.drawable.noone)
            }else{
                Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${dataItem.usuario.champfav}.png?image=q_auto,w_140&v=1585730185").into( itemView.ivcomentario)
            }

            itemView.tag = dataItem
            itemView.setOnLongClickListener{
                listener.OnLongClickComentario(dataItem)
            }
        }
    }

    interface OnLongClickListenerComent{
        fun OnLongClickComentario(comentario:Comentario):Boolean{
            return true
        }
    }
}