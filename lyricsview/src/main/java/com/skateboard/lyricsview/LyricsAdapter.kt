package com.skateboard.lyricsview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class LyricsAdapter(private val lyricsList: List<LyricsItem>) : RecyclerView.Adapter<LyricsItemViewHolder>()
{
    private var time = 0

    private var nowPosition = 0

    override fun getItemCount(): Int
    {
        return lyricsList.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LyricsItemViewHolder
    {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.lyricsitem_layout, p0, false)

        return LyricsItemViewHolder(itemView)
    }


    override fun onBindViewHolder(p0: LyricsItemViewHolder, p1: Int)
    {
        val lyricsItem = lyricsList[p1]
        p0.bindLyricsItem(lyricsItem)
        if (p0.isInRange(time))
        {
            nowPosition = p1
            p0.refreshLyricsItem()
        }
    }

    fun updateTime(time: Int)
    {
        this.time = time

    }
}