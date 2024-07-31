package com.example.floppybird

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.example.floppybird.R

class bird(private val context: Context) {
    class Bird(context: Context) {
        var x: Float = 100f
        var y: Float = 100f
        var velocity: Float = 0f
        private var birdBitmap: Bitmap

        init {
            //Loads the bird image from Drawable file and resizes it
            birdBitmap = BitmapFactory.decodeResource(context.resources,
                R.drawable.bird3new)
            birdBitmap = resizeBitmap(birdBitmap, 180, 190)
        }

        fun update() {
            y += velocity
            velocity += 0.5f //Gravity
        }

        fun flap() {
            velocity = -10f
        }

        private fun resizeBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
        }

        fun getRectangle(): Rect {
            return Rect(x.toInt(), y.toInt(), (x + birdBitmap.width).toInt(),
                (y + birdBitmap.height).toInt())
        }

        fun getBitmap(): Bitmap {
            return birdBitmap
        }
    }
}
