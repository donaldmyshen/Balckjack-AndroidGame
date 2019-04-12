package com.example.balckjack_mingyuan_fangying.game

import android.content.Context
import com.example.balckjack_mingyuan.R


class CardMapper {
    fun getIDs(context: Context): ArrayList<Int> {
        val res = ArrayList<Int>()
        val fields = R.drawable::class.java.declaredFields


        for (i in 0 until fields.size) {
            try {
                val resourceId = fields[i].getInt(R.drawable())
                val name = context.resources.getResourceEntryName(resourceId)

                if (name.matches(Regex("(clubs|joker|spades|diamonds|hearts).*"))) {
                    res.add(resourceId)
                }
            }
            catch (e: Exception) {
                var mes = e.localizedMessage
                continue
            }
        }

        return res
    }
}