package net.azarquiel.lolplus.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.size
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_elegir_foto.*
import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.model.Campeon
import net.azarquiel.lolplus.model.Usuario

class ElegirFotoActivity : AppCompatActivity(), View.OnClickListener {

    private var ivmatriz = Array(10) { arrayOfNulls<ImageView>(5)}
    private lateinit var campeones: List<Campeon>
    private lateinit var usuarioShare: SharedPreferences
    private lateinit var usuariosAL: ArrayList<Usuario>
    private lateinit var usuario: Usuario
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elegir_foto)
        db = FirebaseFirestore.getInstance()
        title="ELIGE TU ICONO"
        usuarioShare = getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        cargarUsuario()
        getData()
    }
    private fun getData() {
        db.collection("campeones")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    makeFotos()
                } else {
                    Log.w("david","Error getting documents.", task.exception)
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

    private fun  makeFotos() {
        var cont = 0
        for (i in 0 until lv.size){
            val lh = lv.getChildAt(i) as LinearLayout
            for (j in 0 until lh.size) {
                ivmatriz[i][j] = lh.getChildAt(j) as ImageView
                ivmatriz[i][j]!!.tag = campeones[cont]
                Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${campeones[cont].nombre}.png?image=q_auto,w_140&v=1585730185").into( ivmatriz[i][j])

                ivmatriz[i][j]!!.setOnClickListener(this)
                cont++
            }
        }
    }

    override fun onClick(v: View?) {
        val imagenPulsada = v as ImageView
        val camp = imagenPulsada.tag as Campeon
        usuario.champfav= camp.nombre
        saveUsuario(usuario)
        db.collection("users").document(usuario.nick).update("champfav",camp.nombre )
        val myintent = Intent(this, MainActivity::class.java)
        startActivity(myintent)
    }

    private fun cargarUsuario () {
        val usuariosShare = usuarioShare.all
        usuariosAL = ArrayList()
        for (entry in usuariosShare.entries) {
            val jsonUsuario = entry.value.toString()
            val user = Gson().fromJson(jsonUsuario, Usuario::class.java)
            usuariosAL.add(user)
        }
        usuario= usuariosAL[0]
    }
    private fun saveUsuario (u:Usuario) {
        val edit = usuarioShare.edit()
        edit.putString(u.nick, Gson().toJson(u))
        edit.apply()
    }

}
