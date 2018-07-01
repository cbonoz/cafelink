package www.cafelink.com.cafelink.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.places.PlaceManager
import com.facebook.places.model.PlaceFields
import com.facebook.places.model.PlaceSearchRequestParams
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import mu.KLogging
import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.CafeApplication.Companion.MY_PERMISSIONS_ACCESS_FINE_LOCATION
import www.cafelink.com.cafelink.R

// https://developers.google.com/maps/documentation/android-sdk/start
// https://developer.android.com/training/location/receive-location-updates
// https://developers.facebook.com/docs/places/android/search
class MapsFragment : Fragment(), OnMapReadyCallback, GraphRequest.Callback {

    private var requestingLocationUpdates: Boolean = false

    private lateinit var mMap: GoogleMap
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var searchMeterRadius: Int = 1000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_maps, container, false)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_area) as SupportMapFragment
        mapFragment.getMapAsync(this)
        makePlaceSearchRequest()
        setupLocationService()
        return v
    }

    private fun setupLocationService() {
        fusedLocationClient = FusedLocationProviderClient(activity as Context)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    logger.debug { "Detected Location: ${location}" }
                    // Update UI with location data
                    // ...
                }
            }
        }
    }

    // Open the cafe message fragment when the user clicks on a cafe marker.
    private fun goToCafeMessageFragment() {

    }


    private fun makePlaceSearchRequest() {
        PlaceManager.newPlaceSearchRequest(createSearchRequest(searchMeterRadius), object : PlaceManager.OnRequestReadyCallback {
            override fun onLocationError(error: PlaceManager.LocationError?) {
                val message = "Error getting location: ${error.toString()}"
                logger.error { message }
            }

            override fun onRequestReady(graphRequest: GraphRequest?) {
                // Sets the callback and executes the request.
                if (graphRequest != null) {
                    graphRequest.setCallback(this@MapsFragment);
                    graphRequest.executeAsync();
                } else {
                    Toast.makeText(activity, "Cafe request could not be completed", Toast.LENGTH_LONG).show()
                    logger.error { "Graph request null in callback" }
                }
            }
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        mMap.

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        startLocationUpdates()
    }

    // Facebook graph response complete.
    override fun onCompleted(response: GraphResponse?) {
        response.toString()
        logger.info { "Graph response: ${response.toString()}" }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    private fun setupLocationUpdates() {
        val context = activity as Context
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity as Activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_ACCESS_FINE_LOCATION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            startLocationUpdates()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {MY_PERMISSIONS_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startLocationUpdates()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(activity, getString(R.string.location_required), Toast.LENGTH_LONG).show()
                    setupLocationUpdates()
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = null
        try {
//            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
        } catch (e: SecurityException) {
            setupLocationUpdates()
        }
    }


    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createSearchRequest(distanceMeters: Int) = PlaceSearchRequestParams.Builder()
            .setSearchText(CafeApplication.CAFE_SEARCH_STRING)
            .setDistance(distanceMeters)
            .setLimit(10)
            .addField(PlaceFields.PICTURE)
            .addField(PlaceFields.LOCATION)
            .addField(PlaceFields.PHONE)
            .addField(PlaceFields.NAME)
            .addField(PlaceFields.HOURS)
            .build()



    companion object: KLogging()

}
