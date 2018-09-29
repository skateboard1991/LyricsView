package com.skateboard.lyricsview

import android.support.v7.widget.RecyclerView
import android.view.View

class LyricsItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    var lyricsItemView: LyricsItemView = itemView.findViewById(R.id.lyricsItemView)

    fun bindLyricsItem(lyricsItem: LyricsItem)
    {
        lyricsItemView.lyricsItem = lyricsItem
    }

    fun isInRange(time:Int):Boolean
    {
        return lyricsItemView.isInrange(time)
    }

    fun refreshLyricsItem()
    {
        lyricsItemView.postInvalidate()
    }
}