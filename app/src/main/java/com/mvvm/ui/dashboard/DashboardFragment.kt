package com.mvvm.ui.dashboard

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import androidx.lifecycle.MutableLiveData
import coil.api.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mvvm.R
import com.mvvm.databinding.FragmentDashboardBinding
import com.mvvm.domain.entity.wrapped.Event
import com.mvvm.extension.*
import com.mvvm.interfaces.UpdateLatLng
import com.mvvm.ui.base.BaseFragment
import com.mvvm.utils.MarkerAnimation
import com.mvvm.vm.OnWeatherUpdateEvent
import com.mvvm.vm.dashboard.DashboardVM
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class DashboardFragment : BaseFragment(AppLayout.fragment_dashboard), OnMapReadyCallback,
GoogleMap.OnMapLoadedCallback {
    private val singleLiveData by inject<MutableLiveData<Event<Bundle>>>()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var rotate: RotateAnimation? = null
    private val dashboardVM by viewModel<DashboardVM>()
    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var marker: BitmapDescriptor? = null

    override fun onCreate(view: View) {
        /*rotate = RotateAnimation(
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

        binding.loading.startAnimation(rotate)*/

        singleLiveData.observe(this, EventUnWrapObserver {
            Timber.d("Event :" + it.getDouble("lat") + it.getDouble("lng"))
            //addMarker(it.getDouble("lat"), it.getDouble("lng"),"test","test 1",marker)
            markerAnimation(LatLng(it.getDouble("lat"), it.getDouble("lng")),14f)
        })
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        marker = getBitmapDescriptor(R.drawable.truck_top_view, requireActivity())
        initMapFragment()
    }

    private fun initMapFragment() {
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            mapFragment!!.getMapAsync(this)
        }
        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment!!).commit()
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

    override fun onMapReady(googleMap: GoogleMap?) {
        Timber.d("onMapReady called")
        mMap = googleMap
        mMap!!.uiSettings.isMapToolbarEnabled = false
        mMap!!.setOnMapLoadedCallback(this)
    }

    override fun onMapLoaded() {
        Timber.d("onMapLoaded called")
        setDefaultMap(mMap)
    }

    private var playMarker: Marker? = null
    private fun markerAnimation(
        currentLocation: LatLng?,
        zoom: Float
    ) {
        try {

            if (playMarker != null) {
                if (currentLocation != null) {
                    MarkerAnimation(mMap, 1000, object : UpdateLatLng {
                        override fun onUpdatedLatLng(updatedLatLng: LatLng?) {}
                    }).animateMarker(currentLocation, playMarker)
                    //speedoMeter(speed)
                        mMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation,
                                mMap!!.cameraPosition.zoom
                            )
                        )

                }
            } else {
                val latLng = LatLng(currentLocation!!.latitude, currentLocation.longitude)
                playMarker = mMap!!.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            currentLocation.latitude,
                            currentLocation.longitude
                        )
                    )
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_top_view))
                )
                mMap!!.setPadding(2000, 4000, 2000, 4000)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
            }
            //moveMarker();
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun addMarker(
        latitude: Double,
        longitude: Double,
        title: String,
        data: String,
        iconResID: BitmapDescriptor?
    ) {
        mMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .title(title)
                .icon(iconResID)
                .snippet(data)
        )
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun getBitmapDescriptor(id: Int, c: Context?): BitmapDescriptor? {
        try {
            val vectorDrawable = c!!.getDrawable(id)
            if (vectorDrawable != null && c != null) {
                val h = vectorDrawable.intrinsicHeight
                val w = vectorDrawable.intrinsicWidth
                vectorDrawable.setBounds(0, 0, w, h)
                val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bm)
                vectorDrawable.draw(canvas)
                return BitmapDescriptorFactory.fromBitmap(bm)
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }
}