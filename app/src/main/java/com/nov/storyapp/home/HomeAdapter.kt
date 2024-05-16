package com.nov.storyapp.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nov.storyapp.data.response.ListStoryItem
import com.nov.storyapp.databinding.StoriesItemBinding
import com.nov.storyapp.helper.toDateFormat

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {
    private var story: List<ListStoryItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.MyViewHolder {
        val binding = StoriesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = story[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = story.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(data: List<ListStoryItem?>?) {
        story = data?.filterNotNull() ?: emptyList()
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: StoriesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            binding.tvName.text = item.name
            binding.tvDesc.text = item.description
            binding.tvDate.text = item.createdAt?.toDateFormat()
            Glide.with(binding.root.context)
                .load(item.photoUrl)
                .into(binding.ivPhoto)
        }
    }
}
