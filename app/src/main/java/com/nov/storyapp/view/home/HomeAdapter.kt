package com.nov.storyapp.view.home

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nov.storyapp.view.detail.DetailStoryActivity
import com.nov.storyapp.data.response.ListStoryItem
import com.nov.storyapp.databinding.StoriesItemBinding
import com.nov.storyapp.helper.toDateFormat

class HomeAdapter : PagingDataAdapter<ListStoryItem, HomeAdapter.MyViewHolder>(HomeDiffCallback) {
    private var story: List<ListStoryItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoriesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
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
            binding.root.setOnClickListener {
                val intentDetailStory = Intent(binding.root.context, DetailStoryActivity::class.java)
                intentDetailStory.putExtra(DetailStoryActivity.ID, item.id)
                binding.root.context.startActivity(intentDetailStory)
            }
        }
    }

    companion object {
        val HomeDiffCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
