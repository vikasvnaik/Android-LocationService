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
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import com.mvvm.listeners.ConnectionListenerLiveData
import com.mvvm.ui.base.BaseFragment
import com.mvvm.utils.CommonUtils
import com.mvvm.utils.MarkerAnimation
import com.mvvm.utils.SphericalUtils
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
    private var prevLatLng: LatLng? = null
    private var prevLatLngAnim: LatLng? = null

    override fun onCreate(view: View) {
        val connectionLiveData = ConnectionListenerLiveData(activityCompat)
        connectionLiveData.observe(this, Observer { isConnected ->
            isConnected?.let {
                if(it){
                    binding.status.snackBar(AppString.your_online)
                } else {
                    binding.status.snackBar(AppString.your_offline)
                }
            }
        })

        singleLiveData.observe(this, EventUnWrapObserver {
            Timber.d("Event :" + it.getDouble("lat") + it.getDouble("lng"))
            //addMarker(it.getDouble("lat"), it.getDouble("lng"),"test","test 1",marker)
            if(prevLatLng == null){
                prevLatLng = LatLng(it.getDouble("lat"), it.getDouble("lng"))
                prevLatLngAnim = LatLng(it.getDouble("lat"), it.getDouble("lng"))
            }
            speedoMeter(it.getFloat("speed"))
            markerAnimation(LatLng(it.getDouble("lat"), it.getDouble("lng")),17f)


            val mDistanceInMeter: Double =
                SphericalUtils.computeDistanceBetween(
                    prevLatLng, LatLng(it.getDouble("lat"), it.getDouble("lng")))
            binding.distance.text = "${getString(AppString.distance)} : " + String.format("%.2f", userDataManager.distance/1000) +" Km"
            if(mDistanceInMeter > 1000) {
                prevLatLng = LatLng(it.getDouble("lat"), it.getDouble("lng"))
                /*dashboardVM.post(
                    OnWeatherUpdateEvent.UpdateWeather(
                        it.getDouble("lat").toString() + "," + it.getDouble("lng").toString()
                    )
                )*/
            }
        })

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
        if(userDataManager.status){
            binding.status.isChecked = true
            binding.status.text = getString(AppString.on_duty)
        } else {
            binding.status.isChecked = false
            binding.status.text = getString(AppString.off_duty)
        }
        binding.status
        binding.status.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                userDataManager.distance = 0.0F
                binding.status.text = getString(AppString.on_duty)
                userDataManager.status = true
                CommonUtils.startLocationService(activityCompat)
                CommonUtils.startWorker(activityCompat)
            } else {
                userDataManager.distance = 0.0F
                binding.status.text = getString(AppString.off_duty)
                userDataManager.status = false
                CommonUtils.stopLocationService(activityCompat)
                CommonUtils.stopWorker(activityCompat)
            }
        }
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

            binding.currentWeather.visibility = View.VISIBLE
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
                    val mDistanceInMeter: Double =
                        SphericalUtils.computeDistanceBetween(
                            prevLatLngAnim, currentLocation)
                    if(mDistanceInMeter > 100) {
                        prevLatLngAnim = currentLocation
                        mMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation,
                                mMap!!.cameraPosition.zoom
                            )
                        )
                    }

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

    private fun speedoMeter(speed: Float ) {
        /**
         * Speed meter implementation on the dashboard
         * */
        /**
         * Speed meter implementation on the dashboard
         */
        binding.speedoMeter.setText(String.format("%.2f", speed))
        binding.speedoMeter.setValueAnimated(Math.round(speed).toFloat())
        /**
         * Handling speed meter color on speed limit
         */
        try {
            //if (getActivity() != null) {
            if (speed >= 60) binding.speedoMeter.setBarColor(
                ContextCompat.getColor(
                    activityCompat,
                    R.color.colorRed
                )
            ) else binding.speedoMeter.setBarColor(
                ContextCompat.getColor(activityCompat, R.color.colorGreen)
            )
            //}
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //CrashReporter.logException(e);
        }
    }
}