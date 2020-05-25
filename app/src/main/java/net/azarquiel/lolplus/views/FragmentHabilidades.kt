package net.azarquiel.lolplus.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.adapters.HabilidadesAdapter
import net.azarquiel.lolplus.model.Campeon
import net.azarquiel.lolplus.model.Habilidad

class FragmentHabilidades : Fragment() {

    private lateinit var champ: Campeon
    private lateinit var adapter: HabilidadesAdapter
    private lateinit var habilidades: ArrayList<Habilidad>
    private lateinit var rvHabilidades: RecyclerView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habilidades, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        val datos = arguments
        champ= datos!!.getSerializable("champ") as Campeon
        rvHabilidades = view.findViewById(R.id.rvHabilidades) as RecyclerView
        initRV()
        getData()

    }
    private fun initRV() {
        adapter = HabilidadesAdapter(requireActivity().baseContext, R.layout.row_habilidades)
        rvHabilidades.adapter = adapter
        rvHabilidades.layoutManager = LinearLayoutManager(activity)
    }
    private fun getData() {
        db.collection("campeones").document(champ.nombre).collection("habilidades")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    adapter.setHabilidades(habilidades)
                } else {
                    Log.w("david","Error getting documents.", task.exception)
                }
            }
    }
    private fun documentToList(documents: QuerySnapshot) {
        habilidades=ArrayList()
        documents.forEach { d ->
            val tecla = d["tecla"] as String
            val nombre = d["nombre"] as String
            val descripcion = d["descripcion"] as String
            val imagen = d["imagen"] as String
            habilidades.add(Habilidad(tecla = tecla,nombre = nombre,descripcion = descripcion, imagen = imagen))
        }
    }

}
