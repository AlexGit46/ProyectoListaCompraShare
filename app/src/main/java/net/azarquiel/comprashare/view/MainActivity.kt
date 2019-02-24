package net.azarquiel.comprashare.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.azarquiel.comprashare.R
import net.azarquiel.comprashare.adapter.CustomAdapter
import net.azarquiel.comprashare.model.Producto
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    private lateinit var carro: SharedPreferences
    private lateinit var carroAL: ArrayList<Producto>
    private lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        getData()
        showData()
        fab.setOnClickListener { dialogoProducto(null) }
    }

    private fun showData() {
        adapter = CustomAdapter(this,R.layout.row,carroAL, carro)
        rvProductos.layoutManager = LinearLayoutManager(this)
        rvProductos.adapter = adapter
    }

    private fun getData() {
        carro = getSharedPreferences("carro", Context.MODE_PRIVATE)
        val carroShare = carro.all
        carroAL = ArrayList()
        for (entry in carroShare.entries)
            carroAL.add(Gson().fromJson<Producto>(entry.value.toString(),Producto::class.java))
        sortAL()

    }

    private fun dialogoProducto(producto: Producto?) {
        alert {
            customView {
                title = if (producto==null) "Add Producto" else "Modify Producto"
                verticalLayout {
                    val etProducto = editText {
                        hint = "Producto"
                        text = if (producto!=null) SpannableStringBuilder(producto.nombre) else SpannableStringBuilder("")
                        padding = dip(20)
                    }
                    val etCantidad = editText {
                        hint = "Cantidad"
                        text = if (producto!=null) SpannableStringBuilder(producto.cantidad) else SpannableStringBuilder("")
                        padding = dip(20)
                    }
                    positiveButton("Aceptar") {
                        if (etProducto.text.toString().length==0 || etCantidad.text.toString().length==0)
                            longToast("Todos los campos son obligatorios...")
                        else
                            if (producto==null)
                                addProducto(Producto(etProducto.text.toString(),etCantidad.text.toString(),false))
                            else
                                mofifyProducto(producto, Producto(etProducto.text.toString(),etCantidad.text.toString(),producto.comprado))
                    }
                    negativeButton("Cancelar"){
                    }
                }
            }
        }.show()
    }

    private fun mofifyProducto(productoviejo: Producto, productonuevo: Producto) {
        val jsonProducto: String = Gson().toJson(productonuevo)
        val editor = carro.edit()
        editor.remove(productoviejo.nombre)
        editor.putString(productonuevo.nombre, jsonProducto)
        editor.commit()
        carroAL.remove(productoviejo)
        carroAL.add(productonuevo)
        sortAL()
        adapter.notifyDataSetChanged()
    }

    private fun addProducto(producto: Producto) {
        val jsonProducto: String = Gson().toJson(producto)
        val editor = carro.edit()
        editor.putString(producto.nombre, jsonProducto)
        editor.commit()
        carroAL.add(producto)
        sortAL()
        adapter.notifyDataSetChanged()
    }

    fun onClickProducto(view: View){
        val producto =view.tag as Producto
        dialogoProducto(producto)
    }

    private fun sortAL() {
        if (carroAL.size==0) return
        val alaux = carroAL.sortedWith(compareBy({it.nombre}))
        carroAL.clear()
        carroAL.addAll(alaux)
    }

}
