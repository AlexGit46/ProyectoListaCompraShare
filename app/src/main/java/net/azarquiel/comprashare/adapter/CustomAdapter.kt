package net.azarquiel.comprashare.adapter

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.row.view.*
import net.azarquiel.comprashare.model.Producto
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class CustomAdapter(val context: Context, val layout: Int, val dataList: ArrayList<Producto>, val carro: SharedPreferences) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(viewlayout: View, val context: Context, val adapter: CustomAdapter) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Producto){
            itemView.ckProducto.setOnCheckedChangeListener(null)
            itemView.ckProducto.text = dataItem.nombre
            itemView.ckProducto.isChecked = dataItem.comprado
            itemView.tvCantidad.text = dataItem.cantidad
            itemView.tag = dataItem

            itemView.ckProducto.setOnCheckedChangeListener  { view, isChecked ->
                dataItem.comprado = isChecked
                val jsonProducto: String = Gson().toJson(dataItem)
                val editor = adapter.carro.edit()
                editor.putString(dataItem.nombre, jsonProducto)
                editor.commit()
                adapter.notifyDataSetChanged()
            }
            itemView.setOnLongClickListener{itemViewOnLongClickListener(dataItem,adapter)}
        }

        private fun itemViewOnLongClickListener(dataItem: Producto, adapter: CustomAdapter): Boolean {
            context.alert("¿Seguro eliminar ${dataItem.nombre}?", "Confirm") {
                yesButton {remove(dataItem,adapter) }
                noButton {}
            }.show()
            return true
        }

        private fun remove(dataItem: Producto, adapter: CustomAdapter) {
            val editor = adapter.carro.edit()
            editor.remove(dataItem.nombre)
            editor.commit()
            adapter.dataList.remove(dataItem)
            adapter.notifyDataSetChanged()
        }
    }
}