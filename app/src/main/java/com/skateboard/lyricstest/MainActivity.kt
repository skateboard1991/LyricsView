package com.skateboard.lyricstest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.skateboard.lyricsview.LyricsItem
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
        for(lyricsItem in lyricsList)
        {
            println("item is "+lyricsItem.lyrics)
        }
    }


    private fun readLrc(): List<LyricsItem>
    {
        val result = mutableListOf<LyricsItem>()
        try
        {
            val lyricInput = BufferedReader(InputStreamReader(assets.open("empty.lrc")))
            var line = lyricInput.readLine()
            while (line != null)
            {

                val lyricItem = parse(line)
                if (result.size > 6)
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

        val pattern = Pattern.compile("^\\[\\w*\\]\\w*$")

        val matcher = pattern.matcher(line)

        if (matcher.find())
        {
            val front = matcher.group(1)

            when
            {
                front.contains("ti") -> lyricsItem.ti = front.split(":")[1]
                front.contains("ar") -> lyricsItem.ar = front.split(":")[1]
                front.contains("al") -> lyricsItem.al = front.split(":")[1]
                front.contains("by") -> lyricsItem.by = front.split(":")[1]
                front.contains("offset")->lyricsItem.offset=front.split(":")[1].toInt()
                else ->
                {
                    val timeArray = front.split(":")
                    lyricsItem.start = timeArray[0].toInt() * 60 + timeArray[1].toFloat().toInt()
                    lyricsItem.lyrics = matcher.group(2)
                }
            }

        }

        return lyricsItem
    }
}
