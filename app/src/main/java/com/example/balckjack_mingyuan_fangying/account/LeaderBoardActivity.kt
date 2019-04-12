package com.example.balckjack_mingyuan_fangying.account

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.balckjack_mingyuan.R
import com.example.balckjack_mingyuan_fangying.game.Player
import com.example.balckjack_mingyuan_fangying.game.PlayerAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_learder_board.*
import kotlinx.android.synthetic.main.content_scrolling.*

class LeaderBoardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learder_board)
        setSupportActionBar(toolbar)
        setupRecyclerView()
        loadPlayers()
    }

    private fun setupRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PlayerAdapter()
            setHasFixedSize(true)
        }
    }

    private fun loadPlayers() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Player")
                .orderBy("winRate", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.d("TAG", error.message)
                        return@addSnapshotListener
                    }

                    val players = snapshots!!.map{
                        it.toObject(Player::class.java)
                    }
                    showPlayersPosition(players)
                }
    }

    private fun showPlayersPosition(players: List<Player>) {
        val adapter = recycler_view.adapter as PlayerAdapter
        adapter.addPlayers(players)
    }
}
