package com.mvvm.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import coil.api.load
import com.mvvm.databinding.FragmentDashboardBinding
import com.mvvm.extension.AppLayout
import com.mvvm.extension.EventObserver
import com.mvvm.extension.activityCompat
import com.mvvm.ui.base.BaseFragment
import com.mvvm.vm.OnWeatherUpdateEvent
import com.mvvm.vm.dashboard.DashboardVM
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class DashboardFragment : BaseFragment(AppLayout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var rotate: RotateAnimation? = null
    private val dashboardVM by viewModel<DashboardVM>()
    override fun onCreate(view: View) {
        rotate = RotateAnimation(
            0F,
            180F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        binding.loadingLayout.setVisibility(View.VISIBLE)
        binding.errorLayout.setVisibility(View.GONE)
        rotate?.setDuration(5000)
        rotate?.setInterpolator(LinearInterpolator())
        rotate?.setFillAfter(true)
        rotate?.setRepeatCount(Animation.INFINITE)

        binding.loading.startAnimation(rotate)
        dashboardVM.post(OnWeatherUpdateEvent.UpdateWeather("13.036621404445428,77.69561364659172"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = _binding ?: FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun observeLiveData() {
        dashboardVM.weatherLiveData.observe(viewLifecycleOwner, EventObserver(activityCompat) {
            Timber.d("Response : $it")
            binding.city.text = it.location.name
            binding.temperature.text = it.current.temperature.toString()
            binding.icon.load(it.current.weather_icons[0])

            rotate!!.cancel()


            binding.loadingLayout.setVisibility(View.GONE)
            binding.currentWeather.setVisibility(View.VISIBLE)
        })
    }
}