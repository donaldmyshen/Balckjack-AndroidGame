package com.example.balckjack_mingyuan_fangying.game

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.example.balckjack_mingyuan.R
import com.example.balckjack_mingyuan_fangying.account.AccountActivity
import com.example.balckjack_mingyuan_fangying.account.LeaderBoardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import kotlin.collections.ArrayList

class GameActivity() : AppCompatActivity(), Parcelable {

    private lateinit var mDetector: GestureDetectorCompat

    private var dealerCards = ArrayList<ImageView>()
    private var playerCards = ArrayList<ImageView>()
    private var player = Player()
    private var dealer = Player()
    private var map = CardMapper()
    lateinit var cardList: ArrayList<Int>

    private var height: Int = 0
    private var width: Int = 0
    private lateinit var faceView: View
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)

    constructor(parcel: Parcel) : this() {}

    var dealerFirst = true
    val rand = Random()
    var r = 0
    var id = 0
    var index = 0
    var dealerIndex = 0
    var dealerFirstCard = 0
    var store = 0
    var BET = 0

    val documentRefrence by lazy {
        val db = FirebaseFirestore.getInstance()
        return@lazy db.document("Player/${FirebaseAuth.getInstance()?.currentUser?.uid}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        faceView = card
        moneyleft.text = "MoneyLeft: ${player.money}"

        cardList = map.getIDs(this)
        // At least five cards
        dealerCards = arrayListOf(dealerCard1, dealerCard2, dealerCard3, dealerCard4, dealerCard5)
        playerCards = arrayListOf(playerCard1, playerCard2, playerCard3, playerCard4, playerCard5)
        card.setImageResource(R.drawable.back)

        for (i in 0 until 4) {
            r = rand.nextInt(cardList.size)
            id = cardList[r]
            val name = resources.getResourceEntryName(id)
            // add card one by one
            if (i % 2 == 0) {
                player.addCard(name)
                val cardValue = findCardValue(name)
                player.getTotal(cardValue)
                if (cardValue == 11 && !player.ace) player.ace = true
                else if (cardValue == 11 && player.ace) {
                    player.totalScore -= 10
                    player.ace = false
                }
                score.text = "Score: " + player.totalScore
                val x = playerCards[index].x
                playerCards[index].translationX = -x;
                playerCards[index].setImageResource(id)
                moveTo(playerCards[index], 0f, 0f);
            }
            else {
                dealer.addCard(name)
                // who first one by one
                if (dealerFirst) {
                    dealerFirstCard = id
                    dealerFirst = false
                }
                val cardValue = findCardValue(name)
                if (cardValue == 11 && !dealer.ace) dealer.ace = true
                else if (cardValue == 11 && dealer.ace) {
                    dealer.totalScore -= 10
                    dealer.ace = false
                }
                dealer.getTotal(cardValue)

                if(index == 0){
                    //figuring out how to make first card back.png
                    val x = dealerCards[index].x
                    dealerCards[index].translationX = -x;
                    dealerCards[index].setImageResource(R.drawable.back)
                    moveTo(dealerCards[index], 0f, 0f);
                    store = id
                }
                else{
                    val x = playerCards[index].x
                    playerCards[index].translationX = -x;
                    dealerCards[index].setImageResource(id)
                    moveTo(playerCards[index], 0f, 0f);
                }
                index++
                dealerIndex++
            }
            cardList.remove(id)
        }

        mDetector = GestureDetectorCompat(this, MyGestureListener())

        val metrics = this.resources.displayMetrics
        this.height = metrics.heightPixels
        this.width = metrics.widthPixels
        // click button to start a new game
        newGame.setOnClickListener {
            restartGame()
        }

        logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@GameActivity, AccountActivity::class.java)
            startActivity(intent)
        }

        leaderboard.setOnClickListener {
            startActivity(Intent(this@GameActivity, LeaderBoardActivity::class.java))
        }

        money_5.setOnClickListener {
            bet.text = "Bet: 5"
        }

        money_10.setOnClickListener {
            bet.text = "Bet: 10"
        }

        money_15.setOnClickListener {
            bet.text = "Bet: 15"
        }

        money_20.setOnClickListener {
            bet.text = "Bet: 20"
        }
    }
    fun moveTo(faceView : View, targetX: Float, targetY: Float) {

        val animSetXY = AnimatorSet()

        val x = ObjectAnimator.ofFloat(
            faceView,
            "translationX",
            faceView.translationX,
            targetX
        )

        val y = ObjectAnimator.ofFloat(
            faceView,
            "translationY",
            faceView.translationY,
            targetY
        )

        animSetXY.playTogether(x, y)
        animSetXY.duration = 300
        animSetXY.start()
    }
    override fun onTouchEvent(event: MotionEvent) : Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun betvalue(){
        if (bet.text == "Bet: 5" ){
            BET = 5
        }
        if (bet.text == "Bet: 10" ){
            BET = 10
        }
        if (bet.text == "Bet: 15" ){
            BET = 15
        }
        if (bet.text == "Bet: 20" ){
            BET = 20
        }
    }

    private fun hit() {
        if(index == 5)
            return
        r = rand.nextInt(cardList.size)
        id = cardList[r]
        val name = resources.getResourceEntryName(id)
        cardList.remove(r)
        player.addCard(name)
        val cardValue = findCardValue(name)
        if (cardValue == 11) player.ace = true
        player.getTotal(cardValue)
        if(player.totalScore > 21 && player.ace) {
            player.totalScore -= 10
            player.ace = false
        }
        score.text = "Score: " + player.totalScore
        val x = playerCards[index].x
        playerCards[index].translationX = -x
        playerCards[index].setImageResource(id)
        moveTo(playerCards[index], 0f, 0f);
        index ++
        if(player.totalScore > 21){
            //score.text = "Busted!"
            declareWinner()
            showEndGameButtons()
        }
    }
    private fun stand() {
        // we help dealer choose a smarter strategy
        while(dealer.getTotal(0) < 17) {
            r = rand.nextInt(cardList.size)
            id = cardList[r]
            val name = resources.getResourceEntryName(id)
            cardList.remove(id)
            dealer.addCard(name)
            val cardValue = findCardValue(name)
            if (cardValue == 11) dealer.ace = true
            dealer.getTotal(cardValue)
            if(dealer.totalScore > 21 && dealer.ace) {
                dealer.totalScore -= 10
                dealer.ace = false
            }
            val x = playerCards[index].x
            playerCards[index].translationX = -x;
            dealerCards[dealerIndex].setImageResource(id)
            moveTo(playerCards[index], 0f, 0f);
            dealerIndex++
        }
        val x = dealerCards[0].x
        dealerCards[0].translationX = -x;
        dealerCards[0].setImageResource(store)
        moveTo( dealerCards[0], 0f, 0f);
        declareWinner()
        showEndGameButtons()
    }
    // gesture control here
    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        private var swipedistance = 150
        // Double tapping allows the Player to stand
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            stand()
            return true
        }
        // Swiping right allows the Player to hit
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if(e2.x - e1.x > swipedistance) {
                hit()
                return true
            }
            return false
        }
    }
    // Get the score of the card by its name, silly way but works well
    // Get the image from the google! Thanks a lot for unknown provider!!!
    private fun findCardValue(a:String) : Int{
        when(a){
            //Clubs
            "clubs2" -> return 2
            "clubs3" -> return 3
            "clubs4" -> return 4
            "clubs5" -> return 5
            "clubs6" -> return 6
            "clubs7" -> return 7
            "clubs8" -> return 8
            "clubs9" -> return 9
            "clubs10" -> return 10
            "clubs_ace" -> return 11
            "clubs_jack" -> return 10
            "clubs_queen" -> return 10
            "clubs_king" -> return 10
            //Diamonds
            "diamonds2" -> return 2
            "diamonds3" -> return 3
            "diamonds4" -> return 4
            "diamonds5" -> return 5
            "diamonds6" -> return 6
            "diamonds7" -> return 7
            "diamonds8" -> return 8
            "diamonds9" -> return 9
            "diamonds10" -> return 10
            "diamonds_ace" -> return 11
            "diamonds_jack" -> return 10
            "diamonds_queen" -> return 10
            "diamonds_king" -> return 10
            //Hearts
            "hearts2" -> return 2
            "hearts3" -> return 3
            "hearts4" -> return 4
            "hearts5" -> return 5
            "hearts6" -> return 6
            "hearts7" -> return 7
            "hearts8" -> return 8
            "hearts9" -> return 9
            "hearts10" -> return 10
            "hearts_ace" -> return 11
            "hearts_jack" -> return 10
            "hearts_queen" -> return 10
            "hearts_king" -> return 10
            //Spades
            "spades2" -> return 2
            "spades3" -> return 3
            "spades4" -> return 4
            "spades5" -> return 5
            "spades6" -> return 6
            "spades7" -> return 7
            "spades8" -> return 8
            "spades9" -> return 9
            "spades10" -> return 10
            "spades_ace" -> return 11
            "spades_jack" -> return 10
            "spades_queen" -> return 10
            "spades_king" -> return 10
        }
        return 0
    }

    // Sets score.text to declare the winner of the round
    private fun declareWinner(){
        // rules
        if(player.totalScore == 21) score.text = "You Win!"
        else if(dealer.totalScore == 21) score.text = "Dealer Wins!"
        else if(player.totalScore < 21 && player.totalScore > dealer.totalScore) score.text = "You Win!"
        else if(dealer.totalScore < 21 && dealer.totalScore > player.totalScore) score.text = "Dealer Wins!"
        else if(dealer.totalScore == 21 && player.totalScore == 21) score.text = "Dealer Wins!"
        else if(player.totalScore == dealer.totalScore) score.text = "Tie!"
        else if(player.totalScore > 21) score.text = "Busted, Dealer Wins!"
        else if(dealer.totalScore > 21) score.text = "Dealer busted, You Win!"

        //todo save it to firebase.
        if (score.text == "You Win!" || score.text == "Dealer busted, You Win!") {
            player.addWinTimes()
            betvalue()
            player.Moneyadd(BET)
            var moneyremain = player.Moneyadd(BET)
            moneyleft.text = "Money Left: ${player.money}"
            documentRefrence.get()
                .addOnSuccessListener {
                    val totalWins = when(it.contains("winTimes")) {
                        true -> { it.get("winTimes", Long::class.java) as Long + 1}
                        false -> 1
                    }

                    documentRefrence.update("winTimes", totalWins)
                        .addOnSuccessListener {
                            Log.d("test", "win added successfully")
                        }
                        .addOnFailureListener {
                            Log.e("test", it.localizedMessage)
                        }
                    documentRefrence.update("moneyremain", moneyremain)
                        .addOnSuccessListener {
                            Log.d("test", "loss added successfully")
                        }
                        .addOnFailureListener {
                            Log.e("test", it.localizedMessage)
                        }
                }
        }

        if (score.text == "Dealer Wins!" || score.text == "Busted, Dealer Wins!") {
            player.addLossTimes()
            betvalue()
            var moneyremain = player.Moneyminus(BET)
            moneyleft.text = "Money Left: ${player.money}"
            documentRefrence.get()
                .addOnSuccessListener {
                    val totalLoss = when(it.contains("lossTimes")) {
                        true -> { it.get("lossTimes", Long::class.java) as Long + 1}
                        false -> 1
                    }

                    documentRefrence.update("lossTimes", totalLoss)
                        .addOnSuccessListener {
                            Log.d("test", "loss added successfully")
                        }
                        .addOnFailureListener {
                            Log.e("test", it.localizedMessage)
                        }
                    documentRefrence.update("moneyremain", moneyremain)
                        .addOnSuccessListener {
                            Log.d("test", "loss added successfully")
                        }
                        .addOnFailureListener {
                            Log.e("test", it.localizedMessage)
                        }
                }
        }

        player.updateWinRate()

        documentRefrence.get()
            .addOnSuccessListener {
                val winRate = player.updateWinRate()
                documentRefrence.update("winRate", winRate)
                    .addOnSuccessListener {
                        Log.d("test", "winRate added successfully")
                    }
                    .addOnFailureListener {
                        Log.e("test", it.localizedMessage)
                    }
            }
    }

    // Restarts the game, new round
    private fun restartGame(){
        finish()
        startActivity(intent)
    }

    // Let new game button visible
    private fun showEndGameButtons(){
        newGame.visibility = View.VISIBLE
        logOut.visibility = View.VISIBLE
        leaderboard.visibility = View.VISIBLE
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GameActivity> {
        override fun createFromParcel(parcel: Parcel): GameActivity {
            return GameActivity(parcel)
        }

        override fun newArray(size: Int): Array<GameActivity?> {
            return arrayOfNulls(size)
        }
    }
}
