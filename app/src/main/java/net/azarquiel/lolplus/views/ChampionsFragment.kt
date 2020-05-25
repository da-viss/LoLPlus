package net.azarquiel.lolplus.views

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import net.azarquiel.lolplus.R
import net.azarquiel.lolplus.adapters.CampeonesAdapter
import net.azarquiel.lolplus.model.Campeon

class ChampionsFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var adapter: CampeonesAdapter
    private lateinit var searchView: SearchView
    private lateinit var campeones: ArrayList<Campeon>
    private lateinit var rvCampeones: RecyclerView
    private var norden =0
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_champions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //if (savedInstanceState == null) {//por problemas de duplicado de menu
        setHasOptionsMenu(true)
        db = FirebaseFirestore.getInstance()
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.show()
        rvCampeones = view.findViewById(R.id.rvCampeones) as RecyclerView
        initRV()
        getData()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu)
// ************* <Filtro> ************
        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(this)
// ************* </Filtro> ************
    }
    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }
    override fun onQueryTextChange(newText: String): Boolean {
        val original = ArrayList<Campeon>(campeones)
        adapter.setCampeones(original.filter { campeon -> campeon.nombre.contains(newText,ignoreCase = true) })
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.orden -> {
                ordenar()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun ordenar(){
        norden++
        if(norden==0){
            adapter.setCampeones(campeones.sortedBy { it.nombre })
            Toast.makeText(context,"Orden alfabetico", Toast.LENGTH_LONG).show()
        }else if(norden==1){
            adapter.setCampeones(campeones.sortedByDescending { it.nombre })
            Toast.makeText(context,"Orden alfabetico descendiente", Toast.LENGTH_LONG).show()
        }else if(norden==2){
            adapter.setCampeones(campeones.sortedBy { it.rol })
            Toast.makeText(context,"Orden por rol", Toast.LENGTH_LONG).show()
        }else if(norden==3){
            adapter.setCampeones(campeones.sortedBy { it.dificultad })
            Toast.makeText(context,"Orden de menor a mayor dificultad", Toast.LENGTH_LONG).show()
            norden=-1
        }
    }

    private fun initRV() {
        adapter = CampeonesAdapter(requireActivity().baseContext, R.layout.row_campeones)
        rvCampeones.adapter = adapter
        rvCampeones.layoutManager = LinearLayoutManager(activity)
    }
    private fun getData() {
        db.collection("campeones")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    adapter.setCampeones(campeones.sortedBy { it.nombre })
                } else {
                    Log.w("david","Error getting documents.", task.exception)
                }
            }
    }
    private fun documentToList(documents: QuerySnapshot) {
        campeones=ArrayList()
        documents.forEach { d ->
            val nombre = d["nombre"] as String
            val historia = d["historia"] as String
            val rol = d["rol"] as String
            val posicion = d["posicion"] as String
            val dificultad = d["dificultad"] as String
            campeones.add(Campeon(nombre = nombre,historia = historia,rol = rol, posicion = posicion, dificultad = dificultad))
        }
    }

}
