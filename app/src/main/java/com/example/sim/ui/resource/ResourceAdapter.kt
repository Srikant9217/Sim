package com.example.sim.ui.resource

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sim.R
import com.example.sim.models.Resource
import kotlinx.android.synthetic.main.item_resource.view.*

class ResourceAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Resource>() {

        override fun areItemsTheSame(oldItem: Resource, newItem: Resource): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Resource, newItem: Resource): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ProductsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_resource,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductsViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Resource>) {
        differ.submitList(list)
    }

    class ProductsViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        private val TAG = "DEBUG"

        fun bind(item: Resource) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            Glide.with(itemView)
                .load(item.image)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_launcher_background)
                .into(image_view)
            itemView.text_view_user_name.text = item.name
            Log.d(TAG, "Resource : $item")
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Resource)
    }
}
