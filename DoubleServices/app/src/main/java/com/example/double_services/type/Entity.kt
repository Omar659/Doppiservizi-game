package com.example.double_services.type

import android.graphics.*

class Entity(
    val speed: MutableList<Float>,
    val position: MutableList<Float>,
    var scale: Float,
    var scaleSpeed: Float,
    val logo: Bitmap?,
    val logoDim: MutableList<Float>,
    val angle: Float,
    val bound: FloatArray,
    var pathBound: Path,
    var m: Matrix,
    var state: Int
) {

    constructor(): this(
        mutableListOf(0f, 0f),
        mutableListOf(0f, 0f),
        0f,
        0f,
        null,
        mutableListOf(0f, 0f),
        0f,
        FloatArray(8),
        Path(),
        Matrix(),
        0
    )

    override fun toString(): String {
        return "Entity(speed=$speed, position=$position, scale=$scale, scaleSpeed=$scaleSpeed, logo=$logo, logoDim=$logoDim, angle=$angle, bound=${bound.contentToString()}, pathBound=$pathBound, m=$m, state=$state)"
    }

}
