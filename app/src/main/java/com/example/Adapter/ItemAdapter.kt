package com.example.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.Model.ModelSinopsisi
import com.example.myapplication.R

class ItemAdapter(private val itemList: ArrayList<ModelSinopsisi>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private lateinit var iListener : onItemClickListener

    interface onItemClickListener{
        fun  onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListtener: onItemClickListener){
        iListener = clickListtener
    }

    class ItemViewHolder(itemView: View, cListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val tvjudul: TextView = itemView.findViewById(R.id.tv_judul)
        val tvtanggal: TextView = itemView.findViewById(R.id.tv_tglupdate)
        val ivgambar: ImageView = itemView.findViewById(R.id.iv_content)

        init {
            itemView.setOnClickListener {
                cListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return ItemViewHolder(view, iListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.tvjudul.text = currentItem.judul
        holder.tvtanggal.text = currentItem.tglupload
        Glide.with(holder.itemView.context)
            .load(currentItem.imgUrl)
            .placeholder(R.drawable.image_default)
            .into(holder.ivgambar)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}