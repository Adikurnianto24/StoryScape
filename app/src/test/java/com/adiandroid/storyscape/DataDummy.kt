package com.adiandroid.storyscape

import com.adiandroid.storyscape.data.model.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryItems(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                name = "author $i",
                description = "quote $i",
                photoUrl = "https://example.com/photo$i.jpg",
                createdAt = "2023-10-01T00:00:00Z",
                lat = 0.0,
                lon = 0.0
            )
            items.add(story)
        }
        return items
    }
}