package com.example.double_services.type

import android.view.MotionEvent

class Bonus(
    val position: MutableList<Float>,
    val text: String,
    val startingTime: Long
    ) {
    constructor(): this(mutableListOf(), "", 0)

    override fun toString(): String {
        return "Bonus(event=$position, text='$text', startingTime=$startingTime)"
    }
}