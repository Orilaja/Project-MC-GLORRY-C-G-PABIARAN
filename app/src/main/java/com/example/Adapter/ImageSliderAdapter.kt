package com.example.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.Model.ModelImage
import com.example.myapplication.databinding.ItemSlideBinding

class ImageSliderAdapter(private val items : List<ModelImage>) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>(){
    inner class ImageViewHolder(itemView: ItemSlideBinding) : RecyclerView.ViewHolder(itemView.root){
        private val binding = itemView
        fun bind(data : ModelImage){
            with(binding){
                Glide.with(itemView)
                    .load(data.imageUrl)
                    .into(ivSlide)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageSliderAdapter.ImageViewHolder {
        return  ImageViewHolder(ItemSlideBinding.inflate(LayoutInflater.from(parent.context),parent,  false))
    }

    override fun onBindViewHolder(holder: ImageSliderAdapter.ImageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}