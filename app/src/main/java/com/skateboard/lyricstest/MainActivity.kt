package com.skateboard.lyricstest

import android.animation.ValueAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.skateboard.lyricsview.LyricsItem
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lyricsList=readLrc()
        lyricsView.lyricsList=lyricsList
        val ani=ValueAnimator.ofInt(180000)
        ani.duration = 180000L
        ani.addUpdateListener {

            val value=it.animatedValue as Int
            lyricsView.update(value.toLong())
        }
//        lyricsView.update(16)
        ani.start()
    }


    private fun readLrc(): List<LyricsItem>
    {
        val result = mutableListOf<LyricsItem>()
        try
        {
            val lyricInput = BufferedReader(InputStreamReader(assets.open("thatgirl.lrc")))
            var line = lyricInput.readLine()
            while (line != null)
            {

                val lyricItem = parse(line)
                if(result.size==6)
                {
                    for(i in 0 until 5)
                    {
                        result[i].start=i*lyricItem.start/5
                        result[i].duration=lyricItem.start/5
                    }
                }
                else if (result.size > 6)
                {
                    result[result.size - 1].duration = lyricItem.start - result[result.size - 1].start
                }
                result.add(lyricItem)
                line=lyricInput.readLine()
            }
            lyricInput.close()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }


        return result
    }

    private fun parse(line: String): LyricsItem
    {

        val lyricsItem = LyricsItem()

        val pattern = Pattern.compile("^(\\[(.*?)\\])(.*?)$")

        val matcher = pattern.matcher(line)

        if (matcher.find())
        {
            val front = matcher.group(2)

            when
            {
                front.contains("ti") -> lyricsItem.ti = front.split(":")[1]
                front.contains("ar") -> lyricsItem.ar = front.split(":")[1]
                front.contains("al") -> lyricsItem.al = front.split(":")[1]
                front.contains("by") -> lyricsItem.by = front.split(":")[1]
                front.contains("offset")->lyricsItem.offset=front.split(":")[1].toLong()
                else ->
                {
                    val timeArray = front.split(":")
                    val secondTimeArray=timeArray[1].split(".")
                    val second=secondTimeArray[0].toLong()
                    val micSecond=secondTimeArray[1].toLong()
                    lyricsItem.start = (timeArray[0].toLong() * 60 + second)*1000+micSecond
                    lyricsItem.lyrics = matcher.group(3)
                }
            }

        }

        return lyricsItem
    }
}
