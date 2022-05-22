package com.rymo.felfel.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.google.android.material.snackbar.Snackbar
import com.rymo.felfel.R
import com.rymo.felfel.configuration.AlarmApplication
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


fun convertDpToPixel(dp: Float, context: Context?): Float {
    return if (context != null) {
        val resources = context.resources
        val metrics = resources.displayMetrics
        dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    } else {
        val metrics = Resources.getSystem().displayMetrics
        dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}

fun formatPrice(
    price: Number,
    unitRelativeSizeFactor: Float = 0.7f
): SpannableString {
    val currencyLabel = "تومان"
    val spannableString = SpannableString("$price $currencyLabel")
    spannableString.setSpan(
        RelativeSizeSpan(unitRelativeSizeFactor),
        spannableString.indexOf(currencyLabel),
        spannableString.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

@SuppressLint("ClickableViewAccessibility")
fun View.implementSpringAnimationTrait() {
    val scaleXAnim = SpringAnimation(this, DynamicAnimation.SCALE_X, 0.90f)
    val scaleYAnim = SpringAnimation(this, DynamicAnimation.SCALE_Y, 0.90f)

    setOnTouchListener { v, event ->
        Timber.i(event.action.toString())
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scaleXAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
                scaleXAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                scaleXAnim.start()

                scaleYAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
                scaleYAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                scaleYAnim.start()

            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                scaleXAnim.cancel()
                scaleYAnim.cancel()
                val reverseScaleXAnim = SpringAnimation(this, DynamicAnimation.SCALE_X, 1f)
                reverseScaleXAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
                reverseScaleXAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                reverseScaleXAnim.start()

                val reverseScaleYAnim = SpringAnimation(this, DynamicAnimation.SCALE_Y, 1f)
                reverseScaleYAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
                reverseScaleYAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                reverseScaleYAnim.start()


            }
        }

        false
    }
}

fun getStatusBarHeight(context: Context?): Int {
    var result = 0
    val resourceId = context!!.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = context.resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun getNavigationBarHeight(activity: Activity): Int {
    val metrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(metrics)
    val usableHeight = metrics.heightPixels
    activity.windowManager.defaultDisplay.getRealMetrics(metrics)
    val realHeight = metrics.heightPixels
    return if (realHeight > usableHeight) realHeight - usableHeight else 0
}

fun getColorOfDegradate(colorStart: String?, colorEnd: String?, percent: Int): Int {
    val color1 = Color.parseColor(colorStart)
    val color2 = Color.parseColor(colorEnd)
    return Color.rgb(
        getColorOfDegradateCalculation(Color.red(color1), Color.red(color2), percent),
        getColorOfDegradateCalculation(Color.green(color1), Color.green(color2), percent),
        getColorOfDegradateCalculation(Color.blue(color1), Color.blue(color2), percent)
    )
}

private fun getColorOfDegradateCalculation(colorStart: Int, colorEnd: Int, percent: Int): Int {
    return (Math.min(colorStart, colorEnd) * (100 - percent) + Math.max(
        colorStart,
        colorEnd
    ) * percent) / 100
}

fun <T> Single<T>.asyncNetworkRequest(): Single<T> {
    return subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun toast(message: String?) {
    Toast.makeText(AlarmApplication.instance!!, message ?: "NULL", Toast.LENGTH_SHORT).show()
}

fun getHeightScreenSize(mContext: Context?): Int {
    var mContext = mContext
    if (mContext == null)
        mContext = AlarmApplication.instance!!
    val displayMetrics = DisplayMetrics()
    val windowManager = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    /*   int height = displayMetrics.heightPixels;
     */
    return displayMetrics.heightPixels
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}


@SuppressLint("HardwareIds")
fun getDeviceID(contentResolver: ContentResolver, callback: (String) -> Unit) {
    val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    callback.invoke(deviceId)
}

fun getRealPathFromURI(contentURI: Uri): String {
    val result: String
    val cursor: Cursor? =
        AlarmApplication.instance!!.getContentResolver().query(contentURI, null, null, null, null)
    if (cursor == null) { // Source is Dropbox or other similar local file path
        result = contentURI.path.toString()
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
    }
    return result
}

fun File.getFileSize(): Long {
    val fileSizeInBytes = this.length()
    val fileSizeInKB = fileSizeInBytes / 1024
    val fileSizeInMB = fileSizeInKB / 1024
    return fileSizeInMB
}


fun <T> List<T>.convertListToMutableList(): MutableList<T> {
    val newList: MutableList<T> = ArrayList(this)
    return newList
}

fun <T> MutableList<T>.convertMutableListToArraylist(): ArrayList<T> {
    val newList: ArrayList<T> = ArrayList(this)
    return newList
}

fun mobileValidate(mobile: String): Boolean {
    val regex = Regex("(\\+98|0)?9\\d{9}")
    return mobile.matches(regex)
}

fun hideKeyboard(activity: Activity) {
    val view = activity.currentFocus
    if (view != null) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken, 0
        )
    }
}

fun shareText(message: String, mContext: Context) {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
    sharingIntent.type = "text/plain"
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
    sharingIntent.putExtra(Intent.EXTRA_TEXT, message)
    mContext.startActivity(Intent.createChooser(sharingIntent, "اشتراک با"))
}

fun getFormattedDateInString(timeInMillis: Long, format: String): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(timeInMillis)
}

fun getFormattedDate(timeInString: String, format: String): Date {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.parse(timeInString)
}

fun startActivity(mActivity: Context, intent: Intent) {
    mActivity.startActivity(intent)
}

fun showSnackBar(rootView: View?, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    rootView?.let {
        val snackBar = Snackbar.make(it, message, duration)
        snackBar.show()
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            AlarmApplication.instance!!.resources.getColor(R.color.black)
        )
        val textMessage =
            snackBarView.findViewById<View>(R.id.snackbar_text) as TextView
        textMessage.setTextColor(AlarmApplication.instance!!.resources.getColor(R.color.white))

    }
}

@SuppressLint("TimberArgCount")
fun Long.getTimeFromLong(): Triple<String, String, String> {

    val hours = java.lang.String.format(
        Locale.ENGLISH, "%02d",
        TimeUnit.MILLISECONDS.toHours(this)
    )
    val minutes = java.lang.String.format(
        Locale.ENGLISH, "%02d",
        TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this))
    )
    val seconds = java.lang.String.format(
        Locale.ENGLISH, "%02d",
        TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
    )

    Timber.e("Times -> $hours : $minutes : $seconds")
    return Triple(hours, minutes, seconds)


}

fun String.convertArabic(): String {
    val chArr = this.toCharArray()
    val sb = StringBuilder()
    for (ch in chArr) {
        if (Character.isDigit(ch)) {
            sb.append(Character.getNumericValue(ch))
        } else if (ch == '٫') {
            sb.append(".")
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

