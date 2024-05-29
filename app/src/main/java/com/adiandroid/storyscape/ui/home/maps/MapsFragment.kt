package com.adiandroid.storyscape.ui.home.maps

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.adiandroid.storyscape.R
import com.adiandroid.storyscape.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private val mapsViewModel: MapsViewModel by activityViewModels()
    private var isMapLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.loadingDescTv.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.mapContainer.visibility = View.INVISIBLE

        return root
    }

    override fun onResume() {
        super.onResume()
        if (!isMapLoaded) {
            binding.loadingDescTv.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
            binding.mapContainer.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_style
                )
            )
            if (!success) {
                Log.e("MapsFragmentStyle", "Failed Load Style")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsFragmentStyle", "Style Not Found", e)
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mapsViewModel.listStories.observe(viewLifecycleOwner) { stories ->
            for (story in stories) {
                val location = LatLng(story.lat, story.lon)
                mMap.addMarker(
                    MarkerOptions().position(location)
                        .title(story.name)
                        .snippet(story.description)
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            }
            binding.loadingDescTv.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            binding.mapContainer.visibility = View.VISIBLE
            isMapLoaded = true
        }
    }
}