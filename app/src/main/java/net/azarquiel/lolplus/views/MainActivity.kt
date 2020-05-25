package net.azarquiel.lolplus.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.model.Campeon
import net.azarquiel.lolplus.model.Usuario
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener  {

    private var usuario: Usuario?=null
    private var tema: Int=0
    private lateinit var usuarioShare: SharedPreferences
    private lateinit var temaShare: SharedPreferences
    private lateinit var usuariosAL: ArrayList<Usuario>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initShare()
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || tema==1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { tema() }
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList=null//para que se vean bien los iconos
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setInitialFragment()
        cargarUsuario()
        updateHeader()
    }

    private fun initShare() {
        usuarioShare = getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        temaShare=getSharedPreferences("tema", Context.MODE_PRIVATE)
        tema = temaShare.getInt("tema",0)
    }
    private fun tema(){
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            tema=0
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            tema=1
        }
        val edit=temaShare.edit()
        edit.putInt("tema",tema)
        edit.apply()

        finish()
        val intent = intent
        startActivity(intent)
    }

    private fun updateHeader() {
        val miivavatar = nav_view.getHeaderView(0).ivavatar
        val mitvavatar = nav_view.getHeaderView(0).tvavatar
        //aqui meteriamos el seleccionar una foto o un nombre
        if(usuario!=null){
            mitvavatar.text = usuario!!.nick
            if(usuario!!.champfav=="noone"){
                miivavatar.setImageResource(R.drawable.noone)
            }else{
                Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${usuario!!.champfav}.png?image=q_auto,w_140&v=1585730185").into( miivavatar)
            }
        }else{
            miivavatar.setImageResource(R.drawable.noone)
            mitvavatar.text = "¿Quién eres?"
        }

        miivavatar.setOnClickListener{
            cargarUsuario()
            if(usuario!=null){
                val myintent = Intent(this, ElegirFotoActivity::class.java)
                startActivity(myintent)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.nav_Inicio -> {
                fragment = InicioFragment()
            }
            R.id.nav_Champions -> {
                fragment = ChampionsFragment()
            }
            R.id.nav_Meta -> {
                fragment = MetaFragment()
            }
            R.id.nav_user -> {
                fragment = UserFragment()
            }
            R.id.nav_perfil -> {
                cargarUsuario()
                fragment = if(usuario!=null){
                    PerfilFragment()
                }else{
                    Toast.makeText(this,"No hay ninguna sesión activa", Toast.LENGTH_LONG).show()
                    UserFragment()
                }
            }
            R.id.nav_exit -> {
                comprobarCerrarSesion()
                fragment = UserFragment()
            }
        }
        replaceFragment(fragment!!)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setInitialFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(
            R.id.frame,
            InicioFragment()
        )
        fragmentTransaction.commit()
    }

    private fun comprobarCerrarSesion(){
        cargarUsuario()
        if(usuario!=null){
            alert("¿Estás seguro de que quieres desconectarte?", "Salir") {
                yesButton {
                    deleteUsuarioShare(usuario!!)
                    updateHeader()
                    FirebaseAuth.getInstance().signOut()
                }
                noButton {}
            }.show()
        }else{
            Toast.makeText(this,"No hay ninguna sesión activa", Toast.LENGTH_LONG).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun clickCampeon(v: View){
        cargarUsuario()
        if(usuario!=null) {
            val campeonp = v.tag as Campeon
            val myintent = Intent(this, DetalleActivity::class.java)
            myintent.putExtra("campeon", campeonp)
            startActivity(myintent)
        }else{
            Toast.makeText(this,"Para ver detalles debes conectarte", Toast.LENGTH_LONG).show()
        }
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
    private fun deleteUsuarioShare (u: Usuario) {
        val edit = usuarioShare.edit()
        toast("Se ha cerrado la sesión de ${usuario!!.nick}")
        edit.remove(u.nick)
        edit.apply()
        usuario=null
    }
}
