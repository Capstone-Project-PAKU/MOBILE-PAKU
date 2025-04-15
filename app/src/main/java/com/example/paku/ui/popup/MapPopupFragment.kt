package com.example.paku.ui.popup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.paku.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson

class MapPopupFragment : DialogFragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var backBtn: Button
    private var googleMap: GoogleMap? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    data class Location(val latitude: Double, val longitude: Double)

    companion object {
        fun newInstance(location: String?): MapPopupFragment {
            val fragment = MapPopupFragment()
            val args = Bundle()
            val jsonString = """$location"""
            val gson = Gson()
            val loc = gson.fromJson(jsonString, Location::class.java)
            args.putDouble("lat", loc.latitude)
            args.putDouble("lng", loc.longitude)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getDouble("lat")
            longitude = it.getDouble("lng")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.popup_cek_map, container, false)
        mapView = view.findViewById(R.id.locationPreview)
        backBtn = view.findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commit()
        }
        MapsInitializer.initialize(requireContext())
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Log.d("TestManja", "$latitude $longitude")
        val location = LatLng(latitude, longitude) // Example: Surabaya
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        map.addMarker(MarkerOptions().position(location).title("Lokasi Presensi"))
    }

    // Forward lifecycle events to mapView
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
