package com.nov.storyapp

import com.nov.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "photoUrl + $i",
                "name $i",
                "description $i",
                "id $i"
            )
            items.add(quote)
        }
        return items
    }
}
