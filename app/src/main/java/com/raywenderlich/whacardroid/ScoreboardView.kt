package com.raywenderlich.whacardroid

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.scoreboard_view.view.*

class ScoreboardView(context: Context, attrs: AttributeSet? = null, defStyle: Int = -1)
  : FrameLayout(context, attrs, defStyle) {

  init {
    inflate(context, R.layout.scoreboard_view, this)

    start_btn.setOnClickListener {
      it.isEnabled = false
      onStartTapped?.invoke()
    }
  }

  var onStartTapped: (() -> Unit)? = null
  var score: Int = 0
    set(value) {
      field = value
      score_counter.text = value.toString()
    }

  var life: Int = 0
    set(value) {
      if (field == 0 && value > field) {
        // Game has been restarted, hide game over message
        gameover.visibility = GONE
      }
      field = value
      life_counter.text = value.toString()

      // If player has 0 lives, show a game over message,
      // re enable start btn and change it's mesasge
      if (value <= 0) {
        gameover.visibility = View.VISIBLE
        start_btn.isEnabled = true
        start_btn.setText(R.string.restart)
      }
    }
}
