package net.azarquiel.lolplus.views

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_comentarios.*

import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.adapters.ComentAdapter
import net.azarquiel.lolplus.model.Campeon
import net.azarquiel.lolplus.model.Comentario
import net.azarquiel.lolplus.model.Usuario
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentComentarios : Fragment(),ComentAdapter.OnLongClickListenerComent {

    private lateinit var listener: ComentAdapter.OnLongClickListenerComent
    private lateinit var champ: Campeon
    private lateinit var adapter: ComentAdapter
    private lateinit var comentarios: List<Comentario>
    private lateinit var usuarios: ArrayList<Usuario>
    private lateinit var rvComentarios: RecyclerView
    private lateinit var usuarioShare: SharedPreferences
    private lateinit var usuariosAL: ArrayList<Usuario>
    private lateinit var usuario: Usuario
    private lateinit var db: FirebaseFirestore
    private var contcoment=0
    private var norden =0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comentarios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        db = FirebaseFirestore.getInstance()
        val datos = arguments
        champ= datos!!.getSerializable("champ") as Campeon
        usuarioShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        rvComentarios = view.findViewById(R.id.rvComentarios) as RecyclerView
        listener=this
        initRV()
        setListener()
        cargarUsuario()
        btn_enviar.setOnClickListener{sendComentario()}
    }

    private fun initRV() {
        adapter = ComentAdapter(requireActivity().baseContext, R.layout.row_comentario,listener)
        rvComentarios.adapter = adapter
        rvComentarios.layoutManager = LinearLayoutManager(activity)
    }
    private fun setListener() {
        val docRef = db.collection("campeones").document(champ.nombre).collection("comentarios")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                documentToList(snapshot.documents)
                actualizar()

            } else {
                Log.d("TAG", "Current data: null")
                comentarios=ArrayList()
                adapter.setComentarios(comentarios)
            }
        }
    }
    private fun documentToList(documents: List<DocumentSnapshot>) {
        val champs=ArrayList<Comentario>()
        comentarios=ArrayList()
        documents.forEach { d ->
            val ncoment = d.id
            val usuario = d["usuario"] as String
            val fecha = d["fecha"] as String
            val comentario = d["comentario"] as String
            champs.add(Comentario(ncoment = ncoment.toInt(),usuario = Usuario(usuario),fecha = fecha, comentario = comentario))
        }
        comentarios = if(norden==0){
            ArrayList(champs).sortedBy { it.ncoment }
        }else{
            ArrayList(champs).sortedByDescending { it.ncoment }
        }
    }

    private fun sendComentario(){
        val sdf= SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val fecha= sdf.format(Date())
        val coment=etcomentario.text.toString()

        val comentario: MutableMap<String, Any> = HashMap() // diccionario key value
        comentario["usuario"] = usuario.nick
        comentario["fecha"] = fecha
        comentario["comentario"] = coment
        contcoment++
        db.collection("campeones").document(champ.nombre).collection("comentarios").document(contcoment.toString()).set(comentario)

        etcomentario.setText("")
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.second, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ordensecond -> {
                ordenar()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun ordenar(){
        norden++
        if(norden==0){
            adapter.setComentarios(comentarios.sortedBy { it.ncoment})
            Toast.makeText(context,"Orden por comentario mas antiguo", Toast.LENGTH_LONG).show()
        }else if(norden==1){
            adapter.setComentarios(comentarios.sortedByDescending { it.ncoment })
            Toast.makeText(context,"Orden por comentario mas reciente", Toast.LENGTH_LONG).show()
            norden=-1
        }
    }

    private fun actualizar() {
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToUsuarios(task.result!!)
                    rellenar()
                } else {
                    Log.w("TAG","Error getting documents.", task.exception)
                }
            }
    }
    private fun documentToUsuarios(documents: QuerySnapshot) {
        usuarios=ArrayList()
        documents.forEach { d ->
            val nick = d["nick"] as String
            val champfav = d["champfav"] as String
            val posicionfav = d["posicionfav"] as String
            val finicio = d["finicio"] as String
            val estado = d["estado"] as String
            usuarios.add(Usuario(nick = nick,champfav = champfav,posicionfav = posicionfav, finicio = finicio, estado = estado))
        }
    }
    private fun rellenar(){
        contcoment=0
        for(c in comentarios){
            for(u in usuarios){
                if(c.usuario.nick==u.nick){
                    c.usuario=u
                }
            }
        }
        contcoment=comentarios.last().ncoment
        adapter.setComentarios(comentarios)
    }

    override fun OnLongClickComentario(comentario: Comentario): Boolean {
        if(comentario.usuario.nick==usuario.nick){
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar comentario")
            builder.setMessage("¿Estás seguro de que quieres eliminar este comentario?")

            builder.setPositiveButton("OK") { dialog, whichButton ->
                db.collection("campeones").document(champ.nombre).collection("comentarios").document(comentario.ncoment.toString())
                    .delete()
                    .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
            }
            builder.setNegativeButton("CANCELAR") { dialog, whichButton ->}
            builder.show()
        }
        return true
    }

}
