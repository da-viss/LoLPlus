package net.azarquiel.lolplus.views

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_perfil.*

import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.model.Usuario
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PerfilFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var usuarioShare: SharedPreferences
    private lateinit var usuariosAL: ArrayList<Usuario>
    private lateinit var usuario: Usuario
    private lateinit var db: FirebaseFirestore
    private var posiciones = arrayListOf ("ADC","MID","TOP","JUN","SUPP")
    private lateinit var pseleccionada:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
        usuarioShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        cargarUsuario()
        cargarDatos()
        btnguardarperfil.setOnClickListener{savePerfil()}
        btnfecha.setOnClickListener { clickDatePicker()  }
    }

    private fun cargarDatos(){
        tvnombreperfil.text= usuario.nick
        tvchampfavperfil.text=usuario.champfav
        etestadoperfil.setText(usuario.estado)
        tvfechaperfil.text= usuario.finicio
        var inicio=0
        if(usuario.posicionfav!=""){
            for (i in 0 until posiciones.size) {
                if (posiciones[i]== usuario.posicionfav){//cojo la posicion del concepto que voy a editar
                    inicio=i
                    pseleccionada=posiciones[i]//lo guardo
                }
            }
            posiciones.removeAt(inicio)
            posiciones.add(0,pseleccionada)//y meto ese mismo concepto pero el primero
        }else{
            posiciones=arrayListOf ("","ADC","MID","TOP","JUN","SUPP")
        }
        loadSpinner()
    }

    private fun loadSpinner(){
        val conceptostxt = posiciones.mapIndexed { index, posicion -> posicion }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, conceptostxt)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerposicion.onItemSelectedListener = this
        spinnerposicion.adapter = adapter
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        pseleccionada = posiciones[position]
    }

    private fun clickDatePicker() {
        val c = Date()
        var sdf = SimpleDateFormat("dd")
        val dia = sdf.format(c).toInt()
        sdf = SimpleDateFormat("MM")
        val mes = sdf.format(c).toInt()
        sdf = SimpleDateFormat("yyyy")
        val anio = sdf.format(c).toInt()
        val tpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{ view, y, m, d ->
            tvfechaperfil.text = "$d-${m+1}-$y"
        },anio,mes-1,dia)

        tpd.show()

    }

    private fun savePerfil(){
        if(etestadoperfil.text.toString().length>150){
            Toast.makeText(context,"Tu estado tiene ${etestadoperfil.text.toString().length} caracteres", Toast.LENGTH_LONG).show()
        }else{
            usuario.estado=etestadoperfil.text.toString()
            usuario.finicio=tvfechaperfil.text.toString()
            usuario.posicionfav=pseleccionada
            //usuario= Usuario(usuario.nick,usuario.champfav,"","",etestadoperfil.text.toString())
            saveUsuario(usuario)
            db.collection("users").document(usuario.nick)
                .update(mapOf(
                    "estado" to etestadoperfil.text.toString(),
                    "finicio" to usuario.finicio,
                    "posicionfav" to usuario.posicionfav
                ))
            Toast.makeText(context,"Perfil actualizado correctamente", Toast.LENGTH_LONG).show()
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
        usuario= usuariosAL[0]
    }
    private fun saveUsuario (u:Usuario) {
        val edit = usuarioShare.edit()
        edit.putString(u.nick, Gson().toJson(u))
        edit.apply()
    }

}
