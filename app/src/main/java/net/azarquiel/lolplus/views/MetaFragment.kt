package net.azarquiel.lolplus.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.size
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_meta.*

import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.model.Campeon
import net.azarquiel.lolplus.model.Usuario

class MetaFragment : Fragment(), View.OnClickListener  {
    companion object {
        const val TAG = "campeon"
    }
    private var ivmatriz = Array(5) { arrayOfNulls<ImageView>(3)}
    private val bestchamp = arrayListOf ("Maokai","Mordekaiser","Renekton","Zac","Elise","Ekko","Galio","Cassiopeia","Kassadin","Ezreal","Varus","Vayne","Blitzcrank","Leona","Zilean")
    private lateinit var usuarioShare: SharedPreferences
    private lateinit var usuariosAL: ArrayList<Usuario>
    private var usuario: Usuario?=null
    lateinit var campeones: List<Campeon>
    private lateinit var champ: Campeon
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meta, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
        usuarioShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        cargarUsuario()
        getData()
    }

    private fun  makeFotos() {
        var cont = 0
        for (i in 0 until lvmeta.size){
            val lh = lvmeta.getChildAt(i) as LinearLayout
            for (j in 0 until lh.size) {
                ivmatriz[i][j] = lh.getChildAt(j) as ImageView
                ivmatriz[i][j]!!.tag = bestchamp[cont]
                Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${bestchamp[cont]}.png?image=q_auto,w_140&v=1585730185").into( ivmatriz[i][j])
                ivmatriz[i][j]!!.setOnClickListener(this)
                cont++
            }
        }
    }
    private fun getData() {
        db.collection("campeones")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    makeFotos()
                } else {
                    Log.w(
                        TAG,"Error getting documents.", task.exception
                    )
                }
            }
    }
    private fun documentToList(documents: QuerySnapshot) {
        val champs=ArrayList<Campeon>()
        documents.forEach { d ->
            val nombre = d["nombre"] as String
            val historia = d["historia"] as String
            val rol = d["rol"] as String
            val posicion = d["posicion"] as String
            val dificultad = d["dificultad"] as String
            champs.add(Campeon(nombre = nombre,historia = historia,rol = rol, posicion = posicion, dificultad = dificultad))
        }
        campeones=ArrayList(champs).sortedBy { it.nombre }
    }

    private fun cargarUsuario () {
        val usuariosShare = usuarioShare.all
        usuariosAL = ArrayList()
        for (entry in usuariosShare.entries) {
            val jsonUsuario = entry.value.toString()
            val user = Gson().fromJson(jsonUsuario, Usuario::class.java)
            usuariosAL.add(user)
        }

        if(usuariosAL.isNotEmpty()){
            usuario= usuariosAL[0]
        }
    }

    override fun onClick(v: View?) {
        val imagenPulsada = v as ImageView
        val camp = imagenPulsada.tag as String
        for (c in campeones){
            if(camp==c.nombre){
                champ=c
            }
        }
        if(usuario!=null) {
            val myintent = Intent(context, DetalleActivity::class.java)
            myintent.putExtra("campeon", champ)
            startActivity(myintent)
        }else{
            Toast.makeText(context,"Para ver detalles debes conectarte", Toast.LENGTH_LONG).show()
        }

    }
}
