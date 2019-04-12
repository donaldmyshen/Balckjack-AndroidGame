package com.example.balckjack_mingyuan_fangying


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.example.balckjack_mingyuan.R
import com.example.balckjack_mingyuan_fangying.account.AccountActivity
import com.example.balckjack_mingyuan_fangying.account.NoConnectionFragment
import com.example.balckjack_mingyuan_fangying.game.GameActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val firebaseAuth : FirebaseAuth by lazy {
        return@lazy  FirebaseAuth.getInstance()
    }

    private var isNetworkConnected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        // Load Fragment into View
        val fm = supportFragmentManager

        // add
        val ft = fm.beginTransaction()

        if (networkInfo == null) {
            Log.e("NETWORK", "not connected")
            ft.add(
                R.id.frag_placeholder,
                NoConnectionFragment()
            )
        }

        if (firebaseAuth != null && firebaseAuth?.currentUser == null) {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            return
        }
        else {
            Log.e("NETWORK", "connected")
            this.isNetworkConnected = true
            imageView.setOnClickListener {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            }
        }
        ft.commit()

        btn_log_out?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val db = FirebaseFirestore.getInstance()

        if(firebaseAuth.currentUser == null)
            finish()

        db.document("Player/${firebaseAuth.currentUser?.uid}")
            .get()
            .addOnSuccessListener {
                if(it == null) {
                    win_score_text_view.text = getString(R.string.win_score_message, 0)
                    loss_score_text_view.text = getString(R.string.loss_score_message, 0)
                    return@addOnSuccessListener
                }

                if(!it.contains("winTimes")){
                    win_score_text_view.text = getString(R.string.win_score_message, 0)

                }
                else {
                    val winScore = it.get("winTimes") as Long
                    win_score_text_view.text = getString(R.string.win_score_message, winScore)
                }

                if(!it.contains("lossTimes")) {
                    loss_score_text_view.text = getString(R.string.loss_score_message, 0)
                }
                else {
                    val lossScore = it.get("lossTimes") as Long
                    loss_score_text_view.text = getString(R.string.loss_score_message, lossScore)
                }

            }
    }
}