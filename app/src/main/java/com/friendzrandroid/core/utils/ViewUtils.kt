package com.friendzrandroid.core.utils

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.friendzrandroid.utils.HelperClass.findViewById

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*
import kotlin.concurrent.schedule


fun ImageView.loadImage(url: String) {
    Glide.with(this.context).load(url)
        //.apply(ImageDialog.getRequestOption(this.context))
        //.transition(DrawableTransitionOptions.withCrossFade())
        //.placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
        .placeholder(com.friendzrandroid.R.drawable.ic_large_logo)
        .into(this)

}


fun ImageView.loadImage(url: Uri) {
    Glide.with(this.context).load(url)
        //.apply(ImageDialog.getRequestOption(this.context))
        //.transition(DrawableTransitionOptions.withCrossFade())
        //.placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
        .placeholder(com.friendzrandroid.R.drawable.ic_large_logo)
        .into(this)

}


fun SwipeRefreshLayout.changeColor() {
    this.setColorSchemeColors(this.resources.getColor(com.friendzrandroid.R.color.primary_color))
}


var duration: Long = 100
fun View.hideToTopithAnimation() {

    this.animate()
        .setDuration(duration)
        .translationY(-this.height.toFloat())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                this@hideToTopithAnimation.visibility = View.GONE
            }
        })
}


fun View.showWithAnimation() {
    if (!this.isVisible) {
        this.visibility = View.VISIBLE;
        this.alpha = 0.0f;
    }
    this.animate()
        .translationY(1.0f)
        .alpha(1.0f)
        .setDuration(duration)
        .setListener(null)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE

}

fun Button.showButtonLoading(loadingText: String) {
    this.text = loadingText
}


fun Button.hideButtonLoading(
    completedText: String, activity: Activity,
    action: (() -> Unit)
) {
    this.text = completedText
    Timer().schedule(1000) {
        activity.runOnUiThread {
            action()
        }
    }
}

fun Toast.showToast(activity: Activity?, message: String) {

    val layout = activity?.layoutInflater?.inflate(
        com.friendzrandroid.R.layout.toast_layout,
        activity.findViewById(com.friendzrandroid.R.id.toastContainer)
    )

    // set the text of the TextView of the message
    val textView = layout?.findViewById<TextView>(com.friendzrandroid.R.id.toastMessageView)
    textView?.text = message

    this.apply {
        //setGravity(Gravity.BOTTOM, 0, 50)
        duration = Toast.LENGTH_SHORT
        view = layout
        show()
    }
}


fun Intent.clearStack(): Intent {
    this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    return this
}

fun createDrawableFromView(activity: Activity, view: View): Bitmap? {

    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay
        .getMetrics(displayMetrics)
    view.layoutParams = DrawerLayout.LayoutParams(
        DrawerLayout.LayoutParams.WRAP_CONTENT,
        DrawerLayout.LayoutParams.WRAP_CONTENT
    )
    view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
    view.layout(
        0, 0, displayMetrics.widthPixels,
        displayMetrics.heightPixels
    )
    view.buildDrawingCache()
    val bitmap = Bitmap.createBitmap(
        view.measuredWidth,
        view.measuredHeight, Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    view.draw(canvas)

    return bitmap
}


fun MarkerOptions.loadIcon(context: Context, url: String?) {

    val whiteLabelEventMarker: View =

        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            com.friendzrandroid.R.layout.custom_proximity_pin_white_label,
            null
        )

    var imageView =
        whiteLabelEventMarker.findViewById(com.friendzrandroid.R.id.tv_marker_text) as ImageView



    Glide.with(context)
        .asBitmap()
        .dontTransform()
        .load(url)
        .error(com.friendzrandroid.R.drawable.ic_marker_green) // to show a default icon in case of any errors
        .listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return resource?.let {
                    BitmapDescriptorFactory.fromBitmap(it)
                }?.let {
                    icon(it); true
                } ?: false
            }
        }).submit()


}

fun Context.bitmapDescriptorFromVector(
    @DrawableRes vectorDrawableResourceId: Int,
    colorTint: String?,
    mText: String?
): BitmapDescriptor? {

    val vectorDrawable: Drawable =
        ResourcesCompat.getDrawable(resources, vectorDrawableResourceId, null)!!
    if (colorTint != null)
        vectorDrawable.setTint(Color.parseColor(colorTint))


    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )


    val canvas = Canvas(bitmap)


    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)


    val paint = Paint()
    paint.style = Paint.Style.FILL
    paint.color = Color.BLACK


    if (mText != null) {
        canvas.drawText(mText, 0F, (bitmap.getHeight() / 2).toFloat(), paint)
    }


    vectorDrawable.draw(canvas)








    return BitmapDescriptorFactory.fromBitmap(bitmap)

}
