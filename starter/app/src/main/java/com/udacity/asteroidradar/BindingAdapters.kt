package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.network.Apod

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription = imageView.context.getString(R.string.hazardous_image_content_description)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = imageView.context.getString(R.string.not_hazardous_image_content_description)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("apodImage")
fun bindApodImage(imageView: ImageView, data: Apod?) {
    data?.let {
        if (data.mediaType == "image") {
            Picasso.with(imageView.context).load(data.imgSrcUrl).into(imageView)
            imageView.contentDescription = data.title
        }
    }
}

@BindingAdapter("apodTitle")
fun bindApodTitle(textView: TextView, data: Apod?) {
    data?.let {
        if (data.mediaType == "image") {
            textView.text = data.title
        }
    }
}

@BindingAdapter("progressVisibility")
fun bindVisibilityToDataDisplayed(progressBar: ProgressBar, listDisplayed: Boolean) {
    if(listDisplayed) {
        progressBar.visibility = View.GONE
    } else {
        progressBar.visibility = View.VISIBLE
    }
}