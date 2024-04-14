package io.hotmic.media_player_sample.ui

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

internal fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun getColorStateList(
    context: Context,
    @ColorRes pressedColor: Int,
    @ColorRes enabledColor: Int,
    @ColorRes disabledColor: Int
): ColorStateList {
    return ColorStateList(
        arrayOf(
            intArrayOf(R.attr.state_pressed),
            intArrayOf(R.attr.state_enabled),
            intArrayOf(-R.attr.state_enabled)
        ),
        intArrayOf(
            ContextCompat.getColor(context, pressedColor),
            ContextCompat.getColor(context, enabledColor),
            ContextCompat.getColor(context, disabledColor)
        )
    )
}

internal infix fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable = apply {
    compositeDisposable.add(this)
}