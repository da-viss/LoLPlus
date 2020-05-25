package net.azarquiel.lolplus.views

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_campeon.*

import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.model.Campeon

class FragmentCampeon : Fragment() {
    private lateinit var champ: Campeon
    private lateinit var sound: MediaPlayer
    private var posicion=0
    private var fame=0
    private lateinit var db: FirebaseFirestore

    private lateinit var btnsound: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_campeon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        val datos = arguments
        champ= datos!!.getSerializable("champ") as Campeon
        btnsound = view.findViewById(R.id.btnsound) as Button
        getData()
        cargarDatos()
        loadSound()
        btnsound.setOnClickListener{onClickSound()}
        btnmas.setOnClickListener{v->pasarFoto(v)}
        btnmenos.setOnClickListener{v->pasarFoto(v)}
    }


    private fun cargarDatos(){
        tvnombredetalle.text=champ.nombre
        tvhistoria.text=champ.historia
        Picasso.get().load("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.nombre}_${posicion}.jpg").into( ivfotogrande)
        tvposicion.text=champ.posicion
        var id= context?.resources?.getIdentifier("dif${champ.dificultad}","drawable", requireContext().packageName)
        ivdificultad.setImageResource(id!!)
    }

    private fun loadSound() {// load sonido
        var id= context?.resources?.getIdentifier("${champ.nombre.toLowerCase()}","raw", requireContext().packageName)
        sound= MediaPlayer.create(context, id!!)
    }
    private fun onClickSound(){
        sound.seekTo(0)
        sound.start()
    }

    private fun pasarFoto(v: View) {
        val btn = v as ImageButton

        if(btn.id== R.id.btnmas) {
            posicion++
            if(posicion==3){
                posicion=0
            }
            Picasso.get().load("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.nombre}_${posicion}.jpg").into( ivfotogrande)
        }else{
            posicion--
            if(posicion==-1){
                posicion=2
            }
            Picasso.get().load("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.nombre}_${posicion}.jpg").into( ivfotogrande)
        }
    }

    private fun getData() {
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    tvfame.text=fame.toString()
                } else {
                    Log.w("david","Error getting documents.", task.exception)
                }
            }
    }
    private fun documentToList(documents: QuerySnapshot) {
        documents.forEach { d ->
            val champfav = d["champfav"] as String
            if(champfav==champ.nombre){
                fame++
            }
        }
    }

}
