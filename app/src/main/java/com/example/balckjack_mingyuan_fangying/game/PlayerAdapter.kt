package com.example.balckjack_mingyuan_fangying.game

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.balckjack_mingyuan.R

import kotlinx.android.synthetic.main.item_player.view.*

class PlayerAdapter : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    private var players: MutableList<Player> = mutableListOf()

    override fun getItemCount(): Int = players.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position], position)
    }

    fun addPlayers(players: List<Player>) {
        this.players.apply {
            clear()

            val sortedPlayers = players.sortedWith(comparator = kotlin.Comparator { o1, o2 ->
                val ratioFun = { it : Player ->
                    when {
                        it.lossTimes == 0 -> when (it.winTimes) {
                            0 -> 0f
                            else -> 1f
                        }
                        else -> it.winTimes.toFloat() / it.lossTimes.toFloat()
                    }
                }

                ratioFun(o1).compareTo(ratioFun(o2))
            }).asReversed()

            addAll(sortedPlayers)
        }
        notifyDataSetChanged()
    }

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(player: Player, position: Int) {
            itemView.tv_position.text = (position + 1).toString()
            itemView.tv_name.text = "${player.first_name} ${player.last_name}"
            itemView.tv_winTimes.text = player.winTimes.toString()
            itemView.tv_lossTime.text = player.lossTimes.toString()
            itemView.tv_winRate.text = when {
                player.lossTimes == 0 -> when(player.winTimes) {
                    0 -> 0
                    else -> 1
                }
                else -> "%.1f".format(player.winTimes.toFloat() / player.lossTimes.toFloat())
            }.toString()
        }
    }
}
