package com.example.weatherapplication.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

object ImageLoader {

    fun load(context: Context, imageUrl: String, imageView: ImageView) {
        Glide.with(context).load(imageUrl).into(imageView)
    }

    fun loadBitmap(context: Context, imageUrl: String, callback: (bitmap: Bitmap) -> Unit) {
        Glide.with(context).asBitmap().load(imageUrl).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                callback.invoke(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
    }
}