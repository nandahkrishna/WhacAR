package com.raywenderlich.whacardroid

class Configuration {
  companion object {
    const val COL_NUM = 3
    const val ROW_NUM = 3

    // Min / max amount of time (in ms) between
    // two different droid moves
    const val MIN_MOVE_DELAY_MS = 400L
    const val MAX_MOVE_DELAY_MS = 4000L

    // min / max amount of time (in ms) between
    // pull up and pull down of the same droid
    const val MIN_PULL_DOWN_DELAY_MS = 400L
    const val MAX_PULL_DOWN_DELAY_MS = 2000L

    // Number of moves per time
    const val MOVES_PER_TIME = 3

    const val START_LIVES = 1
  }
}