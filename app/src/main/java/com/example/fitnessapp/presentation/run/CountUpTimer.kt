package com.example.fitnessapp.presentation.run

import android.os.Handler
import android.os.Looper

class CountUpTimer(private val tickLengthInMillis: Long) {

    companion object {
        private const val NO_TICKS = 0
    }

    private var handler: Handler? = null
    private var state = TimerState.STOPPED
    private var onTimerTickListener: OnTimerTickListener? = null
    private var incrementTicksRunnable = Runnable {
        ticks++
        onTimerTickListener?.onTick()
        resume()
    }
    private var ticks = NO_TICKS

    fun start() {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        state = TimerState.STARTED
        resume()
    }

    private fun resume() {
        if (state == TimerState.STARTED) {
            handler?.postDelayed(incrementTicksRunnable, tickLengthInMillis)
        }
    }

    fun stop() {
        state = TimerState.STOPPED
        handler?.removeCallbacks(incrementTicksRunnable)
    }

    fun getTicksInTimeFormat(): String {
        val timeInMillis = ticks * tickLengthInMillis
        val tenthsOfASecond = timeInMillis % 1000 / 10
        val seconds = timeInMillis / 1000 % 60
        val minutes = timeInMillis / (1000 * 60) % 60
        val hours = timeInMillis / (1000 * 60 * 60) % 24
        return String.format("%02d:%02d:%02d,%02d", hours, minutes, seconds, tenthsOfASecond)
    }

    fun getTickInMillis(): Long {
        return ticks * tickLengthInMillis
    }

    fun setTimerTickListener(onTimerTickListener: OnTimerTickListener) {
        this.onTimerTickListener = onTimerTickListener
    }

    enum class TimerState {
        STARTED,
        STOPPED
    }

    interface OnTimerTickListener {
        fun onTick()
    }
}