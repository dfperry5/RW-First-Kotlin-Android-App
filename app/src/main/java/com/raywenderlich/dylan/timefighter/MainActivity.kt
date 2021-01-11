package com.raywenderlich.dylan.timefighter

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    // VIEWS
    internal lateinit var tapMeButton: Button
    internal lateinit var gameScoreTextView: TextView
    internal lateinit var timeLeftTextView: TextView

    // Variables
    internal var score = 0
    internal var gameStarted = false
    // in Miliseconds
    internal val initialCountDown: Long = 5000
    internal val countDownInterval: Long = 1000
    internal val milisecondsInSecond = 1000
    internal lateinit var countDownTimer: CountDownTimer

    internal var timeLeftOnTimer: Long = 60000


    // Way to get similar values together
    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called: Score is $score")
        tapMeButton = findViewById(R.id.tapMeButton)
        gameScoreTextView = findViewById(R.id.gameScoreTextView)
        updateScoreView()
        timeLeftTextView = findViewById(R.id.timeLeftTextView)
        // Add Listender for "On Create"
        tapMeButton.setOnClickListener {view ->
            incrementScore()
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
        } else {
            resetGame()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.actionAbout) {
            showInfo()
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState -- Score: $score and Time Left on Timer: $timeLeftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestory called.")
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.aboutTitle, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    // Restore game after rotation or some other activity destroying event.
    private fun restoreGame() {
        gameScoreTextView.text = getString(R.string.yourScore, score)
        val restoredTimer = timeLeftOnTimer / milisecondsInSecond
        timeLeftTextView.text = getString(R.string.timeLeft, restoredTimer)
        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / milisecondsInSecond
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                // later
                endGame()
            }
        }
        countDownTimer.start()
        gameStarted = true
    }

    private fun resetGame() {
        score = 0
        updateScoreView()
        // timer
        val initialTimeLeft = initialCountDown / milisecondsInSecond
        timeLeftTextView.text = getString(R.string.timeLeft, initialTimeLeft)

        // Create countdown timer
        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / milisecondsInSecond
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                // later
                endGame()
            }
        }

        gameStarted = false

    }
    private fun incrementScore(){
        if (!gameStarted) {
            startGame()
        }
        score += 1
        updateScoreView()
    }


    private fun updateScoreView() {
        val newScore = getString(R.string.yourScore, score)
        gameScoreTextView.text = newScore
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame(){
        Toast.makeText(this, getString(R.string.gameOverMessage, score), Toast.LENGTH_LONG).show()
        resetGame()
    }

}