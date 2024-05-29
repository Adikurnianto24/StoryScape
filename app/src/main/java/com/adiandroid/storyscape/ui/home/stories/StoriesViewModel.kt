package com.adiandroid.storyscape.ui.home.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adiandroid.storyscape.data.datapaging.StoryRepository
import com.adiandroid.storyscape.data.model.response.ListStoryItem

class StoriesViewModel(private val repo: StoryRepository): ViewModel() {
    val getListStory: LiveData<PagingData<ListStoryItem>> =
        repo.getListStories().cachedIn(viewModelScope)

    fun refreshStories() {
        repo.refreshStories()
    }
}