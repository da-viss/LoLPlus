package net.azarquiel.lolplus.views

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.model.Usuario


class UserFragment : Fragment() {

    private lateinit var usuarioShare: SharedPreferences
    private lateinit var usuariosAL: ArrayList<Usuario>
    private var usuario: Usuario?=null
    private var user: FirebaseUser?=null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
        usuarioShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)

        btnreg.setOnClickListener{ v->onClickUser(v)}
        btnlog.setOnClickListener{ v->onClickUser(v)}
    }

    private fun onClickUser(v: View) {
        val btn = v as Button
        cargarUsuario()
        btnlog.isEnabled = false
        btnreg.isEnabled = false
        if(usuario==null){
            if (edUser.text.toString().isEmpty() || edPass.text.toString().isEmpty()){
                Toast.makeText(context, "Campos obligatorios", Toast.LENGTH_LONG).show()
                return
            }
            val email=edUser.text.toString()+"@gmail.com"
            val password=edPass.text.toString()
            if(btn.id== R.id.btnlog) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        user = auth.currentUser
                        obteneruser()
                        Toast.makeText(context,"Logeado con exito", Toast.LENGTH_LONG).show()

                        edPass.setText("")
                        edUser.setText("")
                    } else {
                        val toast: Toast =Toast.makeText(context, "ERROR EN EL LOGEO",Toast.LENGTH_SHORT)
                        toast.view.background.setColorFilter(Color.parseColor("#C32020") , PorterDuff.Mode.SRC_IN)
                        toast.show()
                    }
                }
            }else if(btn.id== R.id.btnreg){
                if(edUser.length()>15){
                    Toast.makeText(context,"El Nick no puede ser tan largo", Toast.LENGTH_LONG).show()
                }else if(edPass.length()<6){
                    Toast.makeText(context,"La contraseña debe tener al menos 6 carácteres", Toast.LENGTH_LONG).show()
                }else{
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            user = auth.currentUser
                            registrarUser()
                            Toast.makeText(context,"Registrado con exito", Toast.LENGTH_LONG).show()

                            edPass.setText("")
                            edUser.setText("")
                        } else {
                            val toast: Toast =Toast.makeText(context, "ERROR EN EL REGISTRO",Toast.LENGTH_SHORT)
                            toast.view.background.setColorFilter(Color.parseColor("#C32020"), PorterDuff.Mode.SRC_IN)
                            toast.show()
                        }
                    }
                }
            }
        }else{
            Toast.makeText(context,"Primero cierra sesion", Toast.LENGTH_LONG).show()
        }
        btnlog.isEnabled = true
        btnreg.isEnabled = true
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
    private fun saveUsuario (u:Usuario) {
        val edit = usuarioShare.edit()
        edit.putString(u.nick, Gson().toJson(u))
        edit.apply()
    }

    private fun registrarUser(){
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(edUser.text.toString()).build()

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                usuario= Usuario(user.displayName.toString())
                db.collection("users").document(usuario!!.nick).set(usuario!!)
                saveUsuario(usuario!!)
                updateHeader(usuario!!)
                Log.d("TAG", "User profile updated.")
            }
        }
    }
    private fun obteneruser(){
        val user = FirebaseAuth.getInstance().currentUser
        val docRef = db.collection("users").document(user!!.displayName.toString())
        docRef.get().addOnSuccessListener { d ->
            if (d != null) {
                val nick = d["nick"] as String
                val champfav = d["champfav"] as String
                val posicionfav = d["posicionfav"] as String
                val finicio = d["finicio"] as String
                val estado = d["estado"] as String
                usuario=Usuario(nick = nick,champfav = champfav,posicionfav = posicionfav, finicio = finicio, estado = estado)
                saveUsuario(usuario!!)
                updateHeader(usuario!!)
            }
        }
    }

    private fun updateHeader(u:Usuario) {

        val navigationView = requireActivity().findViewById<View>(R.id.nav_view) as NavigationView
        val miivavatar = navigationView.getHeaderView(0).ivavatar
        val mitvavatar = navigationView.getHeaderView(0).tvavatar
        if(usuario!!.champfav=="noone"){
            miivavatar.setImageResource(R.drawable.noone)
        }else{
            Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${usuario!!.champfav}.png?image=q_auto,w_140&v=1585730185").into( miivavatar)
        }
        mitvavatar.text = u.nick
    }
}
