package www.cafelink.com.cafelink.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.places.PlaceManager
import com.facebook.places.model.PlaceFields
import com.facebook.places.model.PlaceSearchRequestParams
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.gson.Gson
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point

import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import www.cafelink.com.cafelink.BuildConfig
import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.CafeApplication.Companion.CAFE_DATA
import www.cafelink.com.cafelink.CafeApplication.Companion.LAST_LOCATION_LOC
import www.cafelink.com.cafelink.CafeApplication.Companion.MY_PERMISSIONS_ACCESS_FINE_LOCATION
import www.cafelink.com.cafelink.CafeApplication.Companion.app
import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.fragments.conversation.CafeConversationFragment
import www.cafelink.com.cafelink.models.cafe.CafeResponse
import www.cafelink.com.cafelink.models.cafe.Data
import www.cafelink.com.cafelink.util.PrefManager
import javax.inject.Inject

// https://developer.android.com/training/location/receive-location-updates
// https://developers.facebook.com/docs/places/android/search
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val SEARCH_RADIUS_METERS: Int = 1000
    private val CAMERA_ANIMATION_MS: Int = 3000

    private val cafeMap = HashMap<String, Data>()

    private val REQUEST_CODE_AUTOCOMPLETE = 1;
    private lateinit var mapboxMap: MapboxMap;
    private val geojsonSourceLayerId = "geojsonSourceLayerId";
    private val symbolIconId = "symbolIconId";

    private lateinit var mapView: MapView

    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CafeApplication.injectionComponent.inject(this)
        Mapbox.getInstance(activity as Context, BuildConfig.MapboxKey)
        fusedLocationClient = FusedLocationProviderClient(activity as Context)
        Timber.d("onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_maps, container, false)
        mapView = v.findViewById<MapView>(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this)
        initFabButtons(v);
        return v
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapReady = true

        setupLocationUpdates()
        mapboxMap.setOnMarkerClickListener { it ->
//            Toast.makeText(activity, it.title, Toast.LENGTH_LONG).show()
            val wrapInScrollView = true
            val cafeData = cafeMap[it.title]
            if (cafeData == null) {
                Toast.makeText(activity as Context, "No Detail Available", Toast.LENGTH_SHORT).show()
            } else {

                val dialog = MaterialDialog.Builder(activity as Context)
                        .title(it.title)
                        .customView(R.layout.cafe_detail_dialog, wrapInScrollView)
                        .negativeText(R.string.back)
                        .onNegative { dialog, which ->
                            dialog.dismiss()
                        }
                        .positiveText(R.string.view_conversations)
                        .onPositive { dialog, which ->
                            dialog.dismiss()
                            goToCafeMessageFragment(cafeData)
                        }
                        .show()

                val view = dialog.customView!!
                val cafeNameText = view.findViewById<TextView>(R.id.cafeNameText)
                cafeNameText.text = cafeData.name
                val cafeDetailText = view.findViewById<TextView>(R.id.cafeDetailText)
                val pictureData = cafeData.picture.data

                val imageView = view.findViewById<ImageView>(R.id.cafeImageView)
                imageView.layoutParams.width = pictureData.width * 5
                imageView.layoutParams.height = pictureData.height * 5

                Glide.with(this)
                        .load(pictureData.url)
                        .into(imageView);

//                val detailString = "Information\n$cafeData"

                cafeDetailText.text = cafeData.getInfo()
            }
            true

        }
        // Add the symbol layer icon to map for future use
//        val icon = BitmapFactory.decodeResource(resources, R.drawable.blue_marker_view);
//        mapboxMap.addImage(symbolIconId, icon);

        // Create an empty GeoJSON source using the empty feature collection
        setUpSource();

        // Set up a new symbol layer for displaying the searched location's feature coordinates
        setupLayer();
    }


    private fun setUpSource() {
        val geoJsonSource = GeoJsonSource(geojsonSourceLayerId);
        mapboxMap.addSource(geoJsonSource);
    }

    private fun setupLayer() {
        val selectedLocationSymbolLayer = SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId);
        selectedLocationSymbolLayer.withProperties(PropertyFactory.iconImage(symbolIconId));
        mapboxMap.addLayer(selectedLocationSymbolLayer);
    }

    private var mapReady: Boolean = false

    private fun initFabButtons(view: View) {
        val searchFab = view.findViewById<FloatingActionButton>(R.id.fab_location_search);
        searchFab.setOnClickListener { it: View ->
            if (mapReady) {
                val intent = PlaceAutocomplete.IntentBuilder()
                        .accessToken(BuildConfig.MapboxKey)
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(activity);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            } else {
                Toast.makeText(activity, "Wait for map to finish loading...", Toast.LENGTH_SHORT).show()
            };
        }

        val locationFab = view.findViewById<FloatingActionButton>(R.id.fab_current_location);
        locationFab.setOnClickListener { it: View ->
            if (mapReady) {
               searchWithCurrentLocation()
            } else {
                Toast.makeText(activity, "Wait for map to finish loading...", Toast.LENGTH_SHORT).show()
            };
        }
    }

    // Open the cafe message fragment when the user clicks on a cafe marker.
    private fun goToCafeMessageFragment(cafeData: Data?) {
        if (cafeData == null) {
            Toast.makeText(activity, "No Detail Available", Toast.LENGTH_SHORT).show()
            return
        }

        val args = Bundle()
        args.putString(CAFE_DATA, gson.toJson(cafeData))
        val cafeFragment = CafeConversationFragment()
        cafeFragment.setArguments(args)
        fragmentManager!!.beginTransaction()
            .replace(R.id.fragment_container, cafeFragment)
            .commit()
    }

    private fun makePlaceSearchRequest(location: Location? = null) {
        cafeMap.clear()
        mapboxMap.clear()
        val searchRequest = createSearchRequest(SEARCH_RADIUS_METERS)

        if (location != null) {
            Timber.d("search with location: $location")
            val graphRequest = PlaceManager.newPlaceSearchRequestForLocation(searchRequest, location)
            graphRequest.callback = PlaceSearchRequestCallback(gson)
            graphRequest.executeAsync()
        } else {
            Timber.d("search without location")
            PlaceManager.newPlaceSearchRequest(searchRequest, PlaceSearchRequestCallback(gson))
        }

    }

    private fun setupLocationUpdates() {
        val context = activity as Activity
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(context,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_ACCESS_FINE_LOCATION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            val lastLocation = prefManager.getJson(LAST_LOCATION_LOC, LatLng::class.java, LatLng())
            if (lastLocation.latitude == 0.0 && lastLocation.longitude == 0.0) {
                searchWithCurrentLocation()
            } else {
                updatePositionAndSearch(lastLocation)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above
            val featureCollection = FeatureCollection.fromFeatures(arrayOf(Feature.fromJson(selectedCarmenFeature.toJson())))

            // Retrieve and update the source designated for showing a selected location's symbol layer icon
            val source: GeoJsonSource? = mapboxMap.getSourceAs(geojsonSourceLayerId);
            if (source != null) {
                source.setGeoJson(featureCollection)
            }

            val newPosition = LatLng((selectedCarmenFeature.geometry() as Point).latitude(),
                    (selectedCarmenFeature.geometry() as Point).longitude())
            // Move map camera to the selected location

            updatePositionAndSearch(newPosition)
        }
    }

    private fun updatePositionAndSearch(newPosition: LatLng) {
        Timber.d("updatePositionAndSearch $newPosition")
        val newCameraPosition = CameraPosition.Builder()
                .target(newPosition)
                .zoom(14.0)
                .build()

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), CAMERA_ANIMATION_MS)

        prefManager.saveJson(LAST_LOCATION_LOC, newPosition)
        val newLocation = Location("")
        newLocation.latitude = newPosition.latitude
        newLocation.longitude = newPosition.longitude
        makePlaceSearchRequest(newLocation)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    searchWithCurrentLocation()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(activity, getString(R.string.location_required), Toast.LENGTH_LONG).show()
                    setupLocationUpdates()
                }
            }
        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun searchWithCurrentLocation(force: Boolean = false) {
        if (!mapReady) {
            return
        }

        try {
            Timber.d("searchWithCurrentLocation")

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location == null) {
                    Toast.makeText(activity, "Error getting location, try again later", Toast.LENGTH_SHORT).show()
                } else {
                    updatePositionAndSearch(LatLng(location.latitude, location.longitude))
                }
            }
        } catch (e: SecurityException) {
            setupLocationUpdates()
        }
    }

    private fun createSearchRequest(distanceMeters: Int) = PlaceSearchRequestParams.Builder()
            .setSearchText(CafeApplication.CAFE_SEARCH_STRING)
            .setDistance(distanceMeters)
            .setLimit(20)
            .addField(PlaceFields.PICTURE)
            .addField(PlaceFields.LOCATION)
            .addField(PlaceFields.PHONE)
            .addField(PlaceFields.NAME)
            .addField(PlaceFields.HOURS)
            .build()


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    inner class PlaceSearchRequestCallback(private val gson: Gson) : PlaceManager.OnRequestReadyCallback, GraphRequest.Callback {

        override fun onLocationError(error: PlaceManager.LocationError?) {
            val message = "Error getting location: ${error.toString()}"
            Timber.e(message)
        }

        override fun onRequestReady(graphRequest: GraphRequest?) {
            // Sets the callback and executes the request.
            if (graphRequest != null) {
                graphRequest.callback = this
                graphRequest.executeAsync();
            } else {
                Toast.makeText(app as Context, "Cafe request could not be completed", Toast.LENGTH_LONG).show()
                Timber.e("Graph request null in callback")
            }
        }

        // Facebook graph response complete.
        override fun onCompleted(response: GraphResponse?) {
            Timber.d("onCompleted graphSearch: $response")
            val cafeResponse = gson.fromJson(response!!.rawResponse, CafeResponse::class.java)
            if (cafeResponse == null) {
                Timber.e("cafeResponse is null")
                return
            }
            Timber.d("cafeResponse: $cafeResponse")

            val iconFactory = IconFactory.getInstance(app!!);

            cafeResponse.data.map {
                val icon = iconFactory.fromResource(R.drawable.blue_marker_view_40);
                val options = MarkerOptions()
                        .position(LatLng(it.location.latitude, it.location.longitude))
                        .icon(icon)
                        .title(it.name)
                        .snippet(it.name)

                cafeMap[it.name] = it

                mapboxMap.addMarker(options)
            }
        }
    }
}
