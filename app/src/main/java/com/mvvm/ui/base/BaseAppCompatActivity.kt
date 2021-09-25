package com.mvvm.ui.base

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.NavigationRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.mvvm.R
import com.mvvm.data.remote.RetrofitManager
import com.mvvm.databinding.ActivityBaseBinding
import com.mvvm.domain.entity.wrapped.Event
import com.mvvm.domain.extention.collectEvent
import com.mvvm.domain.extention.mainScope
import com.mvvm.domain.manager.UserPrefDataManager
import com.mvvm.extension.AppString
import com.mvvm.extension.alert
import com.mvvm.extension.snackBar
import com.transo.ken42.driver.extension.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber


abstract class BaseAppCompatActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityBaseBinding.inflate(layoutInflater).apply {
            containerView.addView(layout())
        }
    }

    val navController by lazy {
        //Navigation.findNavController(this, R.id.containerHostFragment)
        navHost().navController
    }

    private val retrofitManager by inject<RetrofitManager>()

    val bundle by lazy {
        intent.extras ?: Bundle()
    }

    val userDataManager by inject<UserPrefDataManager>()

    /***
     *  Add UI View
     */
    abstract fun layout(): View

    /**
     *  Observe LiveData/Flow
     */
    open fun observeLiveData() {

    }

    private val singleLiveData by inject<MutableLiveData<Event<Bundle>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.root.hideKeyboard()
        observeLiveData()
        /*singleLiveData.observe(this, EventUnWrapObserver {
            Timber.d("EventUnWrapObserver")
        })*/

        retrofitManager.tokenExpire.consumeAsFlow().collectEvent {
            mainScope.launch {
                try {
                    alert(R.style.Dialog_Alert) {
                        setCancelable(false)
                        setMessage(it)
                        setPositiveButton(getString(AppString.label_ok)) { _, _ ->
                            /*userDataManager.logOut()
                            startActivity<OnBoardingActivity> {
                                clearTask()
                                clearTop()
                                newTask()
                            }*/
                            finishAffinity()
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }

            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.root.hideKeyboard()
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            navController.navigateUp()
        } else {
            super.onBackPressed()
        }
    }

    fun showLoader() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        binding.progress.visible()
    }

    fun hideLoader() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progress.gone()
    }

    fun snackBar(msg: String) {
        binding.root.snackBar(msg)
    }

    fun snackBar(@StringRes resID: Int) {
        binding.root.snackBar(resID)
    }

    /*fun changeGraph(@NavigationRes resId: Int) {
        val graph = navController.navInflater.inflate(resId)
        graph.startDestination = R.id.otpFragment
        navController.graph = graph
    }*/

    fun changeGraph(@NavigationRes resId: Int, block: (NavGraph) -> Unit) {
        val graph = navController.navInflater.inflate(resId)
        block(graph)
        navController.graph = graph
    }

}

fun AppCompatActivity.showLoader() {
    castAs<BaseAppCompatActivity> {
        //showLoader()
    }
}

fun AppCompatActivity.hideLoader() {
    castAs<BaseAppCompatActivity> {
        hideLoader()
    }
}

fun AppCompatActivity.snackBar(msg: String) {
    castAs<BaseAppCompatActivity> {
        snackBar(msg)
    }
}

fun AppCompatActivity.snackBar(@StringRes resID: Int) {
    castAs<BaseAppCompatActivity> {
        snackBar(resID)
    }
}

fun AppCompatActivity.childFragments() =
    (supportFragmentManager.fragments.first() as? NavHostFragment)?.childFragmentManager?.fragments

fun AppCompatActivity.navHost() = supportFragmentManager.fragments.first() as NavHostFragment