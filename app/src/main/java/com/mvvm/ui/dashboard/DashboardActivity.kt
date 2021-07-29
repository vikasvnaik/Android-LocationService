package com.mvvm.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mvvm.R
import com.mvvm.databinding.ActivityDashboardBinding
import com.mvvm.extension.viewBinding
import com.mvvm.ui.base.BaseAppCompatActivity
import com.mvvm.vm.dashboard.DashboardVM
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardActivity : BaseAppCompatActivity() {
    private val binding by viewBinding(ActivityDashboardBinding::inflate)
    private val dashboardVM by viewModel<DashboardVM>()

    override fun layout() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}