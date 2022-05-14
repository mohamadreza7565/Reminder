package com.rymo.felfel.common

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import com.rymo.felfel.configuration.AlarmApplication
import com.rymo.felfel.R
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.IllegalStateException
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.WindowCompat
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton
import com.rymo.felfel.data.preferences.Setting
import java.util.*


class Base {

    abstract class BaseFragment : Fragment(), BaseView {

        lateinit var navController: NavController

        override val rootView: CoordinatorLayout?
            get() = view as CoordinatorLayout
        override val viewContext: Context?
            get() = context

        override fun onStart() {
            super.onStart()
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this)
        }


        fun internetError(mustShow: Boolean, onRetryClick: (() -> Unit)? = null) {
            setErrorLayout(
                mustShow = mustShow,
                textError = getString(R.string.internetError),
                imageError = R.drawable.ic_no_internet,
                textRetry = getString(R.string.retry)
            ) {
                onRetryClick?.invoke()
            }
        }


        override fun onResume() {
            super.onResume()
            initLanguage()
        }

        private fun initLanguage() {
            setLocale(
                requireContext(),
                Setting.language!!
            )
            if (Setting.language == Constants.LANGUAGE_EN
            )
                requireActivity().window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
            else
                requireActivity().window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }

        override fun onStop() {
            super.onStop()
        }

        fun initNavigation(view: View) {
            navController = Navigation.findNavController(view)
        }

        fun removeStatusBar() {
            requireActivity().window!!.addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        fun showStatusBar() {
            var attrs = requireActivity().window.attributes
            attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN.inv()
            attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS.inv()
            requireActivity().window.attributes = attrs
        }
    }

    abstract class BaseActivity : AppCompatActivity(), BaseView {

        override val rootView: CoordinatorLayout?
            get() {
                val viewGroup = window.decorView.findViewById(android.R.id.content) as ViewGroup
                if (viewGroup !is CoordinatorLayout) {
                    viewGroup.children.forEach {
                        if (it is CoordinatorLayout)
                            return it
                    }
                    throw IllegalStateException("RootView must be instance of CoordinatorLayout")

                } else {
                    return viewGroup
                }
            }
        override val viewContext: Context?
            get() = this

        var w: Window? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this)
            w = window

        }

        fun internetError(mustShow: Boolean, onRetryClick: (() -> Unit)? = null) {
            setErrorLayout(
                mustShow = mustShow,
                textError = getString(R.string.internetError),
                imageError = R.drawable.ic_no_internet,
                textRetry = getString(R.string.retry)
            ) {
                onRetryClick?.invoke()
            }
        }

        fun removeStatusBar(){
            if (Build.VERSION.SDK_INT in 21..29) {
                window.statusBarColor = Color.TRANSPARENT
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            } else if (Build.VERSION.SDK_INT >= 30) {
                window.statusBarColor = Color.TRANSPARENT
                // Making status bar overlaps with the activity
                WindowCompat.setDecorFitsSystemWindows(window, false)
            }
        }


        override fun attachBaseContext(newBase: Context) {

            // Get configuration and resources before onCreate method
            // Get configuration and resources before onCreate method
            val resources: Resources = newBase.resources
            val configuration = Configuration(resources.configuration)
            configuration.uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED
            val context: Context = newBase.createConfigurationContext(configuration)

            // Set locale with configuration saved

            // Set locale with configuration saved
            val lng = Setting.language!!
            val locale =
                Locale(lng)
            Locale.setDefault(locale)
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)


            super.attachBaseContext(newBase)
        }


        override fun onResume() {
            super.onResume()
            initLanguage()
        }

        private fun initLanguage() {
            setLocale(
                this,
                Setting.language!!
            )
            if (Setting.language == Constants.LANGUAGE_EN
            )
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
            else
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }

        fun removeNavAndStatusBar() {
            w!!.addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        override fun onDestroy() {
            super.onDestroy()
        }

    }

    interface BaseView {

        val rootView: CoordinatorLayout?
        val viewContext: Context?

        fun setProgressIndicator(mustShow: Boolean, transparent: Boolean = false) {

            rootView?.let {
                var loadingView = it.findViewById<View>(R.id.loadingView)

                viewContext?.let { context ->
                    if (loadingView == null && mustShow) {
                        loadingView =
                            LayoutInflater.from(context)
                                .inflate(R.layout.view_loading, it, false)
                        if (transparent)
                            loadingView.setBackgroundColor(
                                context.resources.getColor(R.color.transparent)
                            )
                        it.addView(loadingView)
                    }

                    loadingView?.visibility = if (mustShow) View.VISIBLE else View.GONE
                }

            }

        }

        fun setErrorLayout(
            mustShow: Boolean, textError: String = "", bellowToolbar: Boolean = true,
            @DrawableRes imageError: Int? = null, textRetry: String? = null,
            onRetryClick: (() -> Unit)? = null,
        ) {

            rootView?.let {
                var errorView = it.findViewById<View>(R.id.errorView)

                viewContext?.let { context ->
                    if (errorView == null && mustShow) {
                        errorView =
                            LayoutInflater.from(context).inflate(R.layout.view_error, it, false)
                        it.addView(errorView)

                        val layout = errorView.findViewById<LinearLayout>(R.id.layout)
                        val errorIv = errorView.findViewById<ImageView>(R.id.errorIv)
                        val errorTv = errorView.findViewById<TextView>(R.id.errorTv)
                        val retryBtn = errorView.findViewById<MaterialButton>(R.id.retryBtn)

                        if (textRetry != null) {
                            retryBtn.text = textRetry
                        }

                        if (bellowToolbar) {
                            var params =
                                layout?.layoutParams as FrameLayout.LayoutParams
                            params.setMargins(
                                0, convertDpToPixel(100f, AlarmApplication.instance!!).toInt(), 0, 0
                            )
                        }

                        errorView?.visibility = if (mustShow) View.VISIBLE else View.GONE

                        errorTv.text = textError

                        retryBtn.visibility =
                            if (onRetryClick != null) View.VISIBLE else View.GONE
                        retryBtn.setOnClickListener { onRetryClick?.invoke() }

                        if (imageError == null) {
                            errorIv.visibility = View.GONE
                        } else {
                            errorIv.setImageResource(imageError)
                        }

                    }
                    errorView?.visibility = if (mustShow) View.VISIBLE else View.GONE

                }

            }

        }


        @Subscribe(threadMode = ThreadMode.MAIN)
        fun showError(baseException: BaseException) {
            viewContext?.let {
                when (baseException.type) {
                    BaseException.Type.SIMPLE -> {
                        viewContext?.let { context ->
                            toast(
                                baseException.serverMessage ?: context.getString(
                                    R.string.server_error
                                )
                            )
                        }

                    }
                    BaseException.Type.INTERNET -> {
                        viewContext?.let { context ->
                            toast(
                                baseException.serverMessage ?: context.getString(
                                    R.string.server_error
                                )
                            )
                        }
                    }
                    BaseException.Type.AUTH -> {

                    }
                    else -> {}
                }
            }
        }

        fun startActivity(mContext: Context, intent: Intent) {
            mContext.startActivity(intent)
        }


        fun showSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
            rootView?.let {
                val snackBar = Snackbar.make(it, message, duration)
                snackBar.show()
                val snackBarView = snackBar.view
                snackBarView.setBackgroundColor(
                    AlarmApplication.instance!!.resources.getColor(R.color.black)
                )
                val textMessage =
                    snackBarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
                textMessage.setTextColor(AlarmApplication.instance!!.resources.getColor(R.color.white))

            }
        }


    }

    abstract class BaseViewModel : ViewModel() {

        val progressBarLiveData = MutableLiveData<Boolean>()
        val compositeDisposable = CompositeDisposable()

        override fun onCleared() {
            compositeDisposable.clear()
            super.onCleared()
        }

    }

}
