package com.mvvm.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mvvm.domain.manager.UserPrefDataManager
import com.mvvm.extension.activityCompat
import com.mvvm.vm.SharedVM
import com.transo.ken42.driver.extension.hideKeyboard
import org.koin.android.ext.android.inject


abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {

    val navController by lazy {
        findNavController()
    }

    private var isViewCreated = false

    val bundle by lazy {
        arguments ?: Bundle()
    }

    val fragmentActivity by lazy {
        if (activity is AppCompatActivity) {
            activityCompat
        } else {
            null
        }
    }

    val userDataManager by inject<UserPrefDataManager>()
    val sharedVM by inject<SharedVM>()

    open fun observeLiveData() {}

    @SuppressLint("RestrictedApi")
    open fun onSupportNavigateUp() {
        if (navController.backStack.size > 2) {
            navController.navigateUp()
        } else {
            finish()
        }
    }

    abstract fun onCreate(view: View)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        observeLiveData()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.hideKeyboard()
        if (!isViewCreated) {
            isViewCreated = true
            onCreate(view)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.hideKeyboard()
    }

    open fun finish() {
        activityCompat.finish()
    }
}





