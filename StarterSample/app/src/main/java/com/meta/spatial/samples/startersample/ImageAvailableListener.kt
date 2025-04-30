package com.meta.spatial.samples.startersample

import android.media.Image

interface ImageAvailableListener {
    fun onNewImage(image: Image, width: Int, height: Int)
}