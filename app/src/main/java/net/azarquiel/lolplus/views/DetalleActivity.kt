package net.azarquiel.lolplus.views

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detalle.*
import kotlinx.android.synthetic.main.dialogperfil.view.*
import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.model.Campeon
import net.azarquiel.lolplus.model.Comentario

class DetalleActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener
{
    private val datos = Bundle()
    private lateinit var nameapp: String
    private lateinit var campeonselected: Campeon
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        nameapp = resources.getString(R.string.app_name)
        nav_view.setOnNavigationItemSelectedListener(this)

        campeonselected=intent.getSerializableExtra("campeon") as Campeon
        setInitialFragment()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.navigation_champs -> {
                title = "$nameapp - Campeón"
                fragment = FragmentCampeon()
            }
            R.id.navigation_habi-> {
                title = "$nameapp - Habilidades"
                fragment = FragmentHabilidades()
            }
            R.id.navigation_comen-> {
                title = "$nameapp - Foro"
                fragment = FragmentComentarios()
            }
        }
        replaceFragment(fragment!!)
        return true
    }
    private fun setInitialFragment() {
        val frag=FragmentCampeon()
        datos.putSerializable("champ", campeonselected)
        frag.arguments = datos
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame, frag)
        fragmentTransaction.commit()
        title = "$nameapp - Campeón"
    }
    private fun replaceFragment(fragment: Fragment) {
        datos.putSerializable("champ", campeonselected)
        fragment.arguments = datos
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }

    fun clickComentary(v: View){
        val comentariop = v.tag as Comentario
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Perfil de usuario")
        val inflater = layoutInflater
        val perfilLayout = inflater.inflate(R.layout.dialogperfil, null)

        if(comentariop.usuario.champfav=="noone"){
            perfilLayout.ivperfildialog.setImageResource(R.drawable.noone)
        }else{
            Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${comentariop.usuario.champfav}.png?image=q_auto,w_140&v=1585730185").into(perfilLayout.ivperfildialog)
        }
        perfilLayout.tvnombredialog.text=comentariop.usuario.nick
        perfilLayout.tvestadodialog.text=comentariop.usuario.estado
        perfilLayout.tvposicionfavdialog.text=comentariop.usuario.posicionfav
        perfilLayout.tvfechainiciodialog.text=comentariop.usuario.finicio

        builder.setView(perfilLayout)
        builder.setPositiveButton("Aceptar") { dialog, whichButton ->  }
        builder.show()
    }

}
