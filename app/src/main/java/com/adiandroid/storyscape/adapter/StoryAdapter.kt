package com.adiandroid.storyscape.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adiandroid.storyscape.data.model.response.ListStoryItem
import com.adiandroid.storyscape.databinding.StoryItemRowBinding
import com.adiandroid.storyscape.utility.dateFormat
import com.bumptech.glide.Glide

class StoryAdapter :
    PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StoryItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listStories = getItem(position)
        if (listStories != null) {
            onItemClickCallback?.let { holder.bind(story = listStories, it) }
        }
    }

    class ViewHolder(private val binding: StoryItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem, clickCallback: OnItemClickCallback) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(photo)

                storyName.text = story.name
                storyDesc.text = story.description
                storyDate.text = story.createdAt.dateFormat()
                storyCv.setOnClickListener {
                    clickCallback.onItemClicked(
                        story, arrayOf(
                            Pair(photo, "sharedPhoto"),
                            Pair(storyName, "sharedName"),
                            Pair(storyDesc, "sharedDesc"),
                            Pair(storyDate, "sharedDate")
                        )
                    )
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem, sharedViews: Array<Pair<View, String>>)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
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