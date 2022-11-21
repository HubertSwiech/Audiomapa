package com.example.gg_dyplom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.gg_dyplom.MarkerConfig.MarkerSizeFactor
import com.example.gg_dyplom.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import java.io.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val SMALL_SCALE_MAX_ZOOM_LEVEL = 19.24
    }

    var mMap: GoogleMap? = null
    private lateinit var binding: ActivityMapsBinding
    var ttsHelper: TtsHelper? = null
    lateinit var imageOverlay: GroundOverlay
    var floorVisibility: Int = 1
    var clickedLoc = false
    var clickedWar = false
    var clickedPoi = false
    var clickedPoly = false
    var clickedFloor = false
    var clickedPanel = false
    var fragmentManager = supportFragmentManager
    var pointNumber = "" //Numer wybranego punktu
    var targetNumber = "" //Numer wybranego celu
    lateinit var mapCircle: Circle
    lateinit var mapLine: Polyline
    lateinit var lineMarker: Marker
    lateinit var floorNumber: TextView

    //małe przyciski do zmiany punktów
    lateinit var locButton: Button
    lateinit var warButton: Button
    lateinit var poiButton: Button
    lateinit var polyButton: Button
    lateinit var floorButton: Button

    lateinit var bottomButton: Button
    lateinit var commentBtn: Button
    lateinit var scannerBtn: Button

    lateinit var db: Database
    lateinit var dbCom: DatabaseCom

    val roomMap = mutableMapOf<String, List<Double>>()//wszystkie numery pomieszczeń
    val markerMap = mutableMapOf<Int, List<Double>>()//wszystkie markery
    var polygonListN = mutableListOf<LatLng>()//poligon północny
    var polygonListS = mutableListOf<LatLng>()//poligon południowy
    var polygonListE = mutableListOf<LatLng>()//poligon wschodni
    var polygonListW = mutableListOf<LatLng>()//poligon zachodni
    var polygonListC = mutableListOf<LatLng>()//poligon centralny
    var polygonListL = mutableListOf<LatLng>()//poligon łącznik
    var polygonListED = mutableListOf<LatLng>()//poligon wschodni dziedziniec
    var polygonListWD = mutableListOf<LatLng>()//poligon zachodni dziedziniec
    var polygonListA = mutableListOf<LatLng>()//poligon Duża Aula
    var polygonListF = mutableListOf<LatLng>()//poligon wejście - front
    var polygonListEDWejscie = mutableListOf<LatLng>()//poligon wejście - wschodni dziedziniec
    var polygonListWDWejscie = mutableListOf<LatLng>()//poligon wejście - zachodni dziedziniec
    var polygonListInside = mutableListOf<LatLng>()//poligon komunikat na wejście w budynku
    var allPolygonsAtFloor = mutableListOf<Polygon>()
    var allPolygonsOutsideOnGround = mutableListOf<Polygon>()
    var allPolygonsOutsideOnFront = mutableListOf<Polygon>()
    var allPolygonsLibrary = mutableListOf<Polygon>()

    var allFloorPolygons = mutableListOf<Polygon>()
    var polygonFloorList = mutableListOf<LatLng>()//poligon piętra

    var zonesMap = mutableMapOf<String, Polygon>()
    var floorsMap = mutableMapOf<String, Polygon>()

    val markerListLoc0 = mutableListOf<Marker>()
    val markerListLoc1 = mutableListOf<Marker>()
    val markerListLoc2 = mutableListOf<Marker>()
    val markerListLoc3 = mutableListOf<Marker>()
    val markerListLoc4 = mutableListOf<Marker>()
    val markerListLocL1 = mutableListOf<Marker>()
    val markerListLocL2 = mutableListOf<Marker>()
    val markerListLocL3 = mutableListOf<Marker>()

    val markerListWar0 = mutableListOf<Marker>()
    val markerListWar1 = mutableListOf<Marker>()
    val markerListWar2 = mutableListOf<Marker>()
    val markerListWar3 = mutableListOf<Marker>()
    val markerListWar4 = mutableListOf<Marker>()
    val markerListWarL1 = mutableListOf<Marker>()
    val markerListWarL2 = mutableListOf<Marker>()
    val markerListWarL3 = mutableListOf<Marker>()

    val markerListPoi0 = mutableListOf<Marker>()
    val markerListPoi1 = mutableListOf<Marker>()
    val markerListPoi2 = mutableListOf<Marker>()
    val markerListPoi3 = mutableListOf<Marker>()
    val markerListPoi4 = mutableListOf<Marker>()
    val markerListPoiL1 = mutableListOf<Marker>()
    val markerListPoiL2 = mutableListOf<Marker>()
    val markerListPoiL3 = mutableListOf<Marker>()

    val markerListZone0 = mutableListOf<Marker>()
    val markerListZone1 = mutableListOf<Marker>()
    val markerListZone2 = mutableListOf<Marker>()
    val markerListZone3 = mutableListOf<Marker>()
    val markerListZone4 = mutableListOf<Marker>()
    val markerListZoneL1 = mutableListOf<Marker>()
    val markerListZoneL2 = mutableListOf<Marker>()
    val markerListZoneL3 = mutableListOf<Marker>()

    val markerListFloor0 = mutableListOf<Marker>()
    val markerListFloor1 = mutableListOf<Marker>()
    val markerListFloor2 = mutableListOf<Marker>()
    val markerListFloor3 = mutableListOf<Marker>()
    val markerListFloor4 = mutableListOf<Marker>()

    val markerLocListArray = arrayOf(
        markerListLoc0,
        markerListLoc1,
        markerListLoc2,
        markerListLoc3,
        markerListLoc4,
        markerListLocL1,
        markerListLocL2,
        markerListLocL3
    )
    val markerWarListArray = arrayOf(
        markerListWar0,
        markerListWar1,
        markerListWar2,
        markerListWar3,
        markerListWar4,
        markerListWarL1,
        markerListWarL2,
        markerListWarL3
    )
    val markerPoiListArray = arrayOf(
        markerListPoi0,
        markerListPoi1,
        markerListPoi2,
        markerListPoi3,
        markerListPoi4,
        markerListPoiL1,
        markerListPoiL2,
        markerListPoiL3
    )
    val markerZoneListArray = arrayOf(
        markerListZone0,
        markerListZone1,
        markerListZone2,
        markerListZone3,
        markerListZone4,
        markerListZoneL1,
        markerListZoneL2,
        markerListZoneL3
    )
    val markerFloorListArray = arrayOf(
        markerListFloor0,
        markerListFloor1,
        markerListFloor2,
        markerListFloor3,
        markerListFloor4
    )
    val locationArray0: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val locationArray1: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val locationArray2: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val locationArray3: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val locationArray4: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val locationArrayL1: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val locationArrayL2: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val locationArrayL3: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))

    val warningArray0: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val warningArray1: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val warningArray2: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val warningArray3: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val warningArray4: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val warningArrayL1: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val warningArrayL2: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val warningArrayL3: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))

    val poiArray0: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val poiArray1: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val poiArray2: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val poiArray3: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val poiArray4: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val poiArrayL1: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val poiArrayL2: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val poiArrayL3: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))

    val zoneArray0: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val zoneArray1: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val zoneArray2: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val zoneArray3: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val zoneArray4: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val zoneArrayL1: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val zoneArrayL2: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))
    val zoneArrayL3: MutableMap<Int, List<Double>> = mutableMapOf(0 to listOf(0.0, 0.0))

    private var markerSizeFactor = MarkerSizeFactor.SMALL.value
    private var currentZoomLevel = 0f

    lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

// hide action bar on the top of the screen
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        ttsHelper = TtsHelper(this.applicationContext, this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    @SuppressLint("ResourceType")
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setMapClickListener()
        googleMap.setUpCameraMoveListener()
        mMap = googleMap
        val polygons =
            readJson("strefy_all.json")//wczytuje poligony korytarzy z .json do strumienia
        val myPolygons =
            Gson().fromJson(polygons, PolygonModelClass::class.java)//pobiera markery ze strumienia

        val floors = readJson("pietra.json")
        val myFloors = Gson().fromJson(floors, PolygonModelClass::class.java)

        val rooms = readJson("rooms_centroids.json")//wczytuje poligony korytarzy z .json do strumienia
        val myRooms = Gson().fromJson(rooms, RoomsModelClass::class.java)//pobiera markery ze strumienia

//        println(myRooms)
        sortingRoomsNumbers(myRooms,)
//        println(roomMap)

        locationArray0.remove(0)
        locationArray1.remove(0)
        locationArray2.remove(0)
        locationArray3.remove(0)
        locationArray4.remove(0)
        locationArrayL1.remove(0)
        locationArrayL2.remove(0)
        locationArrayL3.remove(0)

        warningArray0.remove(0)
        warningArray1.remove(0)
        warningArray2.remove(0)
        warningArray3.remove(0)
        warningArray4.remove(0)
        warningArrayL1.remove(0)
        warningArrayL2.remove(0)
        warningArrayL3.remove(0)

        poiArray0.remove(0)
        poiArray1.remove(0)
        poiArray2.remove(0)
        poiArray3.remove(0)
        poiArray4.remove(0)
        poiArrayL1.remove(0)
        poiArrayL2.remove(0)
        poiArrayL3.remove(0)

        zoneArray0.remove(0)
        zoneArray1.remove(0)
        zoneArray2.remove(0)
        zoneArray3.remove(0)
        zoneArray4.remove(0)
        zoneArrayL1.remove(0)
        zoneArrayL2.remove(0)
        zoneArrayL3.remove(0)

        drawMarkers()

        db = Database(this)
        dbCom = DatabaseCom(this)


        mMap?.uiSettings?.isMapToolbarEnabled = false
//        mMap.uiSettings.isCompassEnabled = false

        var mapView = mapFragment.view
        val compassButton: View =
            mapView!!.findViewWithTag("GoogleMapCompass")   //to access the compass button

        val rlp = compassButton.layoutParams as RelativeLayout.LayoutParams
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_END)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_START)
        rlp.topMargin = 250
        rlp.leftMargin = 70


        //ustawia custom'owe info window dla markerów
        mMap?.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(this))
        mMap?.setOnInfoWindowClickListener { marker ->
//            Toast.makeText(this, marker.title.toString(), Toast.LENGTH_SHORT).show()
            val dialog = PopupMenu(marker.title.toString(), db, dbCom)
//            dialog.setStyle(R.style.PopupStyle)

            dialog.show(supportFragmentManager, "customDialog")
        }


        val circleOptions = CircleOptions()
        circleOptions.center(LatLng(0.0, 0.0))
        circleOptions.radius(5.0)
        mMap?.let {
            mapCircle = it.addCircle(circleOptions)
            val polyOptions = PolylineOptions()
            polyOptions.add(LatLng(0.0, 0.0))
            polyOptions.width(5.0F)
            mapLine = it.addPolyline(polyOptions)
            val marOptions = MarkerOptions()
            marOptions.position(LatLng(0.0, 0.0))
            it.addMarker(marOptions)?.let { marker ->
                lineMarker = marker
            }
            //Ustawia styl mapy
            it.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
            //tak raczej nie powinno to wyglądać i nie wiem jak to poprawić
            //być może zrobić osobną klase dla Circle
            //Ustawia pozycje kamery, obrót i zoom
            val cp = CameraPosition.Builder()
                .bearing(-65f)
                .target(LatLng(52.220585, 21.010170))
                .zoom(18.4f).build()
            val cu = CameraUpdateFactory.newCameraPosition(cp)
            it.animateCamera(cu)
        }
        mapCircle.remove()
        mapLine.remove()
        lineMarker.remove()


        setGroundOverlay(R.drawable.pietro1)//Nakłada pierwsze piętro na mapę


        floorNumber = findViewById<TextView>(R.id.floor)//Numer piętra w prawym górnym rogu


        locButton = findViewById(R.id.locBtn)
        warButton = findViewById(R.id.ostBtn)
        poiButton = findViewById(R.id.poiBtn)
        polyButton = findViewById(R.id.polyBtn)
        floorButton = findViewById(R.id.floorBtn)
        bottomButton = findViewById<Button>(R.id.bottomPanelBtn)

        binding.menu.setOnClickListener {
            mapCircle.remove()
            mapLine.remove()
            lineMarker.remove()
            this.window.decorView.rootView.announceForAccessibility("Z lewej strony ekranu otworzono pionową listwę z numerami pięter.")

            val frag = supportFragmentManager.findFragmentById(R.id.fragmentContainerMenu)
            if (frag != null) {
                val transaction: FragmentTransaction = fragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left)
                transaction.remove(frag)
                transaction.commit()
            } else {
                hideBottomPanel(R.id.ContainerBottomPanel)
                replaceFragment(
                    FragmentMenu(floorNumber),
                    R.anim.enter_from_left,
                    R.anim.exit_to_left,
                    R.id.fragmentContainerMenu
                )
            }
        }

        locButton.setOnClickListener {
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
            clickLoc(locButton)
        }
        warButton.setOnClickListener {
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
            if (clickedWar) {
                warButton.setBackgroundResource(R.drawable.marker_invis_btn)
            } else {
                warButton.setBackgroundResource(R.drawable.marker_vis_btn)
            }
            toggleWarningMarkersVisibility()
        }
        poiButton.setOnClickListener {
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
            if (clickedPoi) {
                poiButton.setBackgroundResource(R.drawable.marker_invis_btn)
            } else {
                poiButton.setBackgroundResource(R.drawable.marker_vis_btn)
            }
            togglePoiMarkersVisibility()
        }
        polyButton.setOnClickListener {
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
            clickPoly(polyButton)
        }
        floorButton.setOnClickListener {
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
            clickFloor(floorButton)
        }
        commentBtn = findViewById<Button>(R.id.commentListBtn)
//        settingsBtn = findViewById<Button>(R.id.qrScannerBtn)


        bottomButton.setOnClickListener {
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
            this.window.decorView.rootView.announceForAccessibility("Na dolnej połowie ekranu otworzono panel do odczytywania komunikatów.")
//            commentBtn.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)

            openBottomPanel(bottomButton, floorNumber, db, commentBtn, scannerBtn)
        }


        mMap?.setOnMarkerClickListener { marker ->
            ttsHelper?.stopSpeaking()
            pointNumber = marker.title.toString()
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
            //Jeżeli panel dolny nie jest jeszcze otrzowony to się otwiera
            if (!clickedPanel) {
                openBottomPanel(bottomButton, floorNumber, db, commentBtn, scannerBtn)
                this.window.decorView.rootView.announceForAccessibility("Na dolnej połowie ekranu otworzono panel do odczytywania komunikatów.")
            } else {
                bottomButton.visibility = View.INVISIBLE
                replaceFragment(
                    FragmentBottomPanel(bottomButton, floorNumber, db, commentBtn),
                    0,
                    0,
                    R.id.ContainerBottomPanel
                )
                clickedPanel = true
            }
            false
        }


        mMap?.setOnPolygonClickListener { polygon ->
//            println("qqqqqqqq ${zonesMap.filter { it.value == polygon }.keys}")
            ttsHelper?.stopSpeaking()
            pointNumber =
                zonesMap.filter { it.value == polygon }.keys.elementAt(floorVisibility).toString()
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
//            //Jeżeli panel dolny nie jest jeszcze otrzowony to się otwiera
            if (!clickedPanel) {
                openBottomPanel(bottomButton, floorNumber, db, commentBtn, scannerBtn)
                this.window.decorView.rootView.announceForAccessibility("Na dolnej połowie ekranu otworzono panel do odczytywania komunikatów.")
            } else {
                bottomButton.visibility = View.INVISIBLE
                replaceFragment(
                    FragmentBottomPanel(bottomButton, floorNumber, db, commentBtn),
                    0,
                    0,
                    R.id.ContainerBottomPanel
                )
                clickedPanel = true
            }
            false
        }


        commentBtn.setOnClickListener {
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
            this.window.decorView.rootView.announceForAccessibility("Na całym ekranie otworzono widok z komentarzami.")
            hideBottomPanel(R.id.ContainerBottomPanel)
            replaceFragment(
                FragmentComments(floorNumber, supportFragmentManager),
                R.anim.enter_from_left,
                R.anim.exit_to_left,
                R.id.fragmentContainer
            )
        }


        val searchButton = findViewById<Button>(R.id.searchButton)
        var poiList = mutableMapOf<Int, MutableList<String>>()


        searchButton.setOnClickListener {
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
            db.open()
            poiList = db.getPOI()
            db.close()
            this.window.decorView.rootView.announceForAccessibility("Na całym ekranie otworzono widok wyszukiwania punktów.")
            hideBottomPanel(R.id.ContainerBottomPanel)
            replaceFragment(
                FragmentSearchBar(poiList, floorNumber),
                R.anim.enter_from_top,
                R.anim.exit_to_top,
                R.id.fragmentContainer
            )
        }


        scannerBtn = findViewById(R.id.qrScannerBtn)
        scannerBtn.setOnClickListener{
            if (mapCircle != null) {
                mapCircle.remove()
            }
            if (mapLine != null) {
                mapLine.remove()
            }
            if (lineMarker != null) {
                lineMarker.remove()
            }
            this.window.decorView.rootView.announceForAccessibility("Na całym ekranie otworzono widok ze skanerem kodów QR.")
            hideBottomPanel(R.id.ContainerBottomPanel)
            replaceFragment(
                FragmentQRScanner(floorNumber),
                R.anim.enter_from_left,
                R.anim.exit_to_left,
                R.id.fragmentContainer
            )
        }




        //copy database to app
        copyDatabase("gmach_glowny_nowy.db")
//        copyDatabase("komentarze.db")
    }//================ =======FUNKCJE=====================================================================================


//    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val intentResult: IntentResult =
//            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//        // if the intentResult is null then
//        // toast a message as "cancelled"
//        if (intentResult != null) {
//            if (intentResult.getContents() == null) {
//                Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
//            } else {
//                // if the intentResult is not null we'll set
//                // the content and format of scan message
//                messageText.setText(intentResult.getContents())
//                messageFormat.setText(intentResult.getFormatName())
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data)
//        }
//    }

    override fun onBackPressed()
    {
//        println("klikniete")
        val currentFragmentBottomPanel =this.supportFragmentManager.findFragmentById(R.id.ContainerBottomPanel)
        val currentFragmentMenu =this.supportFragmentManager.findFragmentById(R.id.fragmentContainerMenu)
        val currentFragmentComment =this.supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val currentFragmentSearchBar =this.supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val currentFragmentQRScanner =this.supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if(currentFragmentBottomPanel is FragmentBottomPanel) {
            hideBottomPanel(R.id.ContainerBottomPanel)
        }
        else if(currentFragmentMenu is FragmentMenu){
            hideFragment(R.id.fragmentContainerMenu, R.anim.enter_from_left, R.anim.exit_to_left)
        }
        else if(currentFragmentComment is FragmentComments){
            var frag = supportFragmentManager.findFragmentById(R.id.ButtonAction)
            if(frag!=null){
                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                transaction.remove(frag)
                transaction.commit()
            }
            hideFragment(R.id.fragmentContainer, R.anim.enter_from_right, R.anim.exit_to_left)
            this.window.decorView.rootView.announceForAccessibility("Zamknięto widok z komentarzami.")
            ttsHelper?.stopSpeaking()
        }
        else if(currentFragmentSearchBar is FragmentSearchBar){
            hideFragment(R.id.fragmentContainer, R.anim.enter_from_top, R.anim.exit_to_top)
        }
        else if(currentFragmentQRScanner is FragmentQRScanner){
            hideFragment(R.id.fragmentContainer, R.anim.enter_from_right, R.anim.exit_to_left)
        }
        else{
            val alertDialog = AlertDialog.Builder(this)
//            alertDialog.setTitle("Exit Alert")
            alertDialog.setMessage(Html.fromHtml("<font color='#062e04'> <big> <big> Czy na pewno chcesz wyjść z aplikacji? <br> </big> </big> </font>"))
            alertDialog.setPositiveButton(Html.fromHtml("<font color='#633a0e'> <big> <big> <b> Wyjdź </b> </big> </big> </font>")) { dialog, whichButton ->
                super.onBackPressed()
            }
            alertDialog.setNegativeButton(Html.fromHtml("<font color='#633a0e'> <big> <big> Anuluj </big> </big> </font>")) { dialog, whichButton ->

            }

            alertDialog.show()

        //            super.onBackPressed()
        }
    }


    private fun removeMarkers() {
        markerListFloor0.removeMarkersAndClear()
        markerListFloor1.removeMarkersAndClear()
        markerListFloor2.removeMarkersAndClear()
        markerListFloor3.removeMarkersAndClear()
        markerListFloor4.removeMarkersAndClear()
        markerListZone0.removeMarkersAndClear()
        markerListZone1.removeMarkersAndClear()
        markerListZone2.removeMarkersAndClear()
        markerListZone3.removeMarkersAndClear()
        markerListZone4.removeMarkersAndClear()
        markerListZoneL1.removeMarkersAndClear()
        markerListZoneL2.removeMarkersAndClear()
        markerListZoneL3.removeMarkersAndClear()
        allPolygonsAtFloor.removePolygonsAndClear()
        allPolygonsOutsideOnGround.removePolygonsAndClear()
        allPolygonsOutsideOnFront.removePolygonsAndClear()
        allPolygonsLibrary.removePolygonsAndClear()
        allFloorPolygons.removePolygonsAndClear()
        polygonListN.removePolygonsLatLngAndClear()
        polygonListS.removePolygonsLatLngAndClear()
        polygonListE.removePolygonsLatLngAndClear()
        polygonListW.removePolygonsLatLngAndClear()
        polygonListC.removePolygonsLatLngAndClear()
        polygonListL.removePolygonsLatLngAndClear()
        polygonListED.removePolygonsLatLngAndClear()
        polygonListWD.removePolygonsLatLngAndClear()
        polygonListA.removePolygonsLatLngAndClear()
        polygonListF.removePolygonsLatLngAndClear()
        polygonListEDWejscie.removePolygonsLatLngAndClear()
        polygonListWDWejscie.removePolygonsLatLngAndClear()
        polygonListInside.removePolygonsLatLngAndClear()
        polygonFloorList.removePolygonsLatLngAndClear()

        markerListLoc0.removeMarkersAndClear()
        markerListLoc1.removeMarkersAndClear()
        markerListLoc2.removeMarkersAndClear()
        markerListLoc3.removeMarkersAndClear()
        markerListLoc4.removeMarkersAndClear()
        markerListLocL1.removeMarkersAndClear()
        markerListLocL2.removeMarkersAndClear()
        markerListLocL3.removeMarkersAndClear()
        markerListWar0.removeMarkersAndClear()
        markerListWar1.removeMarkersAndClear()
        markerListWar2.removeMarkersAndClear()
        markerListWar3.removeMarkersAndClear()
        markerListWar4.removeMarkersAndClear()
        markerListWarL1.removeMarkersAndClear()
        markerListWarL2.removeMarkersAndClear()
        markerListWarL3.removeMarkersAndClear()
        markerListPoi0.removeMarkersAndClear()
        markerListPoi1.removeMarkersAndClear()
        markerListPoi2.removeMarkersAndClear()
        markerListPoi3.removeMarkersAndClear()
        markerListPoi4.removeMarkersAndClear()
        markerListPoiL1.removeMarkersAndClear()
        markerListPoiL2.removeMarkersAndClear()
        markerListPoiL3.removeMarkersAndClear()
        locationArray0.clear()
        locationArray1.clear()
        locationArray2.clear()
        locationArray3.clear()
        locationArray4.clear()
        locationArrayL1.clear()
        locationArrayL2.clear()
        locationArrayL3.clear()
        warningArray0.clear()
        warningArray1.clear()
        warningArray2.clear()
        warningArray3.clear()
        warningArray4.clear()
        warningArrayL1.clear()
        warningArrayL2.clear()
        warningArrayL3.clear()
        poiArray0.clear()
        poiArray1.clear()
        poiArray2.clear()
        poiArray3.clear()
        poiArray4.clear()
        poiArrayL1.clear()
        poiArrayL2.clear()
        poiArrayL3.clear()
        markerMap.clear()
    }

    private fun MutableList<Marker>.removeMarkersAndClear() {
        forEach {
            it.remove()
        }
        clear()
    }
    private fun MutableList<Polygon>.removePolygonsAndClear() {
        forEach {
            it.remove()
        }
        clear()
    }
    private fun MutableList<LatLng>.removePolygonsLatLngAndClear() {
        clear()
    }


    private fun drawMarkers(markerConfig: MarkerConfig = MarkerConfig()) {
        val googleMap = mMap ?: return
        val markers = readJson("point_location.json")//wczytuje markery z .json do strumienia
        val myMarkers =
            Gson().fromJson(markers, MarkersModelClass::class.java)//pobiera markery ze strumienia
        sortingMarkers(
            myMarkers,
            0,
            200,
            0,
            locationArray0,
            googleMap,
            markerListLoc0,
            R.drawable.ic_location_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            0,
            200,
            1,
            locationArray1,
            googleMap,
            markerListLoc1,
            R.drawable.ic_location_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            0,
            200,
            2,
            locationArray2,
            googleMap,
            markerListLoc2,
            R.drawable.ic_location_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            0,
            200,
            3,
            locationArray3,
            googleMap,
            markerListLoc3,
            R.drawable.ic_location_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            0,
            200,
            4,
            locationArray4,
            googleMap,
            markerListLoc4,
            R.drawable.ic_location_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            0,
            200,
            5,
            locationArrayL1,
            googleMap,
            markerListLocL1,
            R.drawable.ic_location_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            0,
            200,
            6,
            locationArrayL2,
            googleMap,
            markerListLocL2,
            R.drawable.ic_location_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            0,
            200,
            7,
            locationArrayL3,
            googleMap,
            markerListLocL3,
            R.drawable.ic_location_sign,
            markerConfig
        )

        sortingMarkers(
            myMarkers,
            200,
            300,
            0,
            warningArray0,
            googleMap,
            markerListWar0,
            R.drawable.ic_warning_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            200,
            300,
            1,
            warningArray1,
            googleMap,
            markerListWar1,
            R.drawable.ic_warning_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            200,
            300,
            2,
            warningArray2,
            googleMap,
            markerListWar2,
            R.drawable.ic_warning_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            200,
            300,
            3,
            warningArray3,
            googleMap,
            markerListWar3,
            R.drawable.ic_warning_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            200,
            300,
            4,
            warningArray4,
            googleMap,
            markerListWar4,
            R.drawable.ic_warning_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            200,
            300,
            5,
            warningArrayL1,
            googleMap,
            markerListWarL1,
            R.drawable.ic_warning_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            200,
            300,
            6,
            warningArrayL2,
            googleMap,
            markerListWarL2,
            R.drawable.ic_warning_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            200,
            300,
            7,
            warningArrayL3,
            googleMap,
            markerListWarL3,
            R.drawable.ic_warning_sign,
            markerConfig
        )

        sortingMarkers(
            myMarkers,
            300,
            500,
            0,
            poiArray0,
            googleMap,
            markerListPoi0,
            R.drawable.ic_poi_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            300,
            500,
            1,
            poiArray1,
            googleMap,
            markerListPoi1,
            R.drawable.ic_poi_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            300,
            500,
            2,
            poiArray2,
            googleMap,
            markerListPoi2,
            R.drawable.ic_poi_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            300,
            500,
            3,
            poiArray3,
            googleMap,
            markerListPoi3,
            R.drawable.ic_poi_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            300,
            500,
            4,
            poiArray4,
            googleMap,
            markerListPoi4,
            R.drawable.ic_poi_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            300,
            500,
            5,
            poiArrayL1,
            googleMap,
            markerListPoiL1,
            R.drawable.ic_poi_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            300,
            500,
            6,
            poiArrayL2,
            googleMap,
            markerListPoiL2,
            R.drawable.ic_poi_sign,
            markerConfig
        )
        sortingMarkers(
            myMarkers,
            300,
            500,
            7,
            poiArrayL3,
            googleMap,
            markerListPoiL3,
            R.drawable.ic_poi_sign,
            markerConfig
        )
        ///////////////////////////
        val polygons =
            readJson("strefy_all.json")//wczytuje poligony korytarzy z .json do strumienia

        val myPolygons =
            Gson().fromJson(polygons, PolygonModelClass::class.java)//pobiera markery ze strumienia
        val floors = readJson("pietra.json")
        val myFloors = Gson().fromJson(floors, PolygonModelClass::class.java)

        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsAtFloor,
            "north",
            polygonListN,
            "#66D8DE66",
            "#999E2C",
            markerConfig
        )
        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsAtFloor,
            "south",
            polygonListS,
            "#66E25555",
            "#B51616",
            markerConfig
        )
        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsAtFloor,
            "east",
            polygonListE,
            "#6650C1E5",
            "#057AA0",
            markerConfig
        )
        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsAtFloor,
            "west",
            polygonListW,
            "#666BCE5A",
            "#1E7110",
            markerConfig
        )
        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsAtFloor,
            "center",
            polygonListC,
            "#667D4AD3",
            "#5F458C",
            markerConfig
        )
        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsLibrary,
            "library",
            polygonListL,
            "#66B36B3F",
            "#752F05",
            markerConfig
        )
        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsOutsideOnGround,
            "east_yard",
            polygonListED,
            "#66FFFFFF",
            "#FFFFFF",
            markerConfig
        )
        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsOutsideOnGround,
            "west_yard",
            polygonListWD,
            "#66FFFFFF",
            "#FFFFFF",
            markerConfig
        )
        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsOutsideOnGround,
            "aula",
            polygonListA,
            "#66FFFFFF",
            "#FFFFFF",
            markerConfig
        )
        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsOutsideOnFront,
            "front",
            polygonListF,
            "#66FFFFFF",
            "#FFFFFF",
            markerConfig
        )

        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsOutsideOnGround,
            "east_yard_back",
            polygonListEDWejscie,
            "#66FFFFFF",
            "#FFFFFF",
            markerConfig
        )

        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsOutsideOnGround,
            "west_yard_back",
            polygonListWDWejscie,
            "#66FFFFFF",
            "#FFFFFF",
            markerConfig
        )

        addPolygonToMap(
            myPolygons,
            googleMap,
            allPolygonsOutsideOnFront,
            "inside",
            polygonListInside,
            "#66FFFFFF",
            "#FFFFFF",
            markerConfig
        )

        addFloorPolygonToMap(
            myFloors,
            googleMap,
            allFloorPolygons,
            polygonFloorList,
            "#66FFB1FF",
            "#5F458C",
            markerConfig
        )
    }

    private fun toggleMarkersSize() {
        val markerConfig = MarkerConfig()
        if (markerSizeFactor == MarkerSizeFactor.BIG.value) {
            markerConfig.textSize *= markerSizeFactor
            markerConfig.threeDigitsNumTextSizeOffset *= markerSizeFactor
            markerConfig.threeDigitsNumTextPositionX *= markerSizeFactor
            markerConfig.threeDigitsNumTextPositionY *= markerSizeFactor
            markerConfig.twoDigitsNumTextPositionX *= markerSizeFactor
            markerConfig.twoDigitsNumTextPositionY *= markerSizeFactor
            markerConfig.oneDigitsNumTextPositionX *= markerSizeFactor
            markerConfig.oneDigitsNumTextPositionY *= markerSizeFactor
            markerConfig.warningTextPositionX *= markerSizeFactor
            markerConfig.warningTextPositionY *= markerSizeFactor
            markerConfig.warningTextSizeOffset *= markerSizeFactor
        }
        drawMarkers(markerConfig)
    }

    private fun GoogleMap.setUpCameraMoveListener() {
        setOnCameraMoveListener {
            val zoomLevel = cameraPosition.zoom
            if (zoomLevel == currentZoomLevel) {
                return@setOnCameraMoveListener
            }
            currentZoomLevel = zoomLevel
            val newMarkerSizeFactor = if (currentZoomLevel > SMALL_SCALE_MAX_ZOOM_LEVEL) {
                MarkerSizeFactor.BIG.value
            } else {
                MarkerSizeFactor.SMALL.value
            }
            if (newMarkerSizeFactor == markerSizeFactor) {
                return@setOnCameraMoveListener
            }
            markerSizeFactor = newMarkerSizeFactor
            removeMarkers()
            toggleMarkersSize()
            toggleMarkersVisibility()
        }
    }

    private fun toggleMarkersVisibility() {
        if (clickedLoc) {
            clickedLoc = false
            toggleLocationMarkersVisibility()
        }
        if (clickedWar) {
            clickedWar = false
            toggleWarningMarkersVisibility()
        }
        if (clickedPoi) {
            clickedPoi = false
            togglePoiMarkersVisibility()
        }
        if (clickedPoly) {
            clickedPoly = false
            togglePolyMarkersVisibility()
        }
        if (clickedFloor) {
            clickedFloor = false
            toggleFloorMarkersVisibility()
        }
    }

    override fun onPause() {
        ttsHelper?.stopSpeaking()
        super.onPause()
    }

    private fun makePolygonInvisible(polygon: Polygon) {
        polygon.isVisible = false
        polygon.isClickable = false
    }

    private fun makePolygonVisible(polygon: Polygon) {
        polygon.isVisible = true
        polygon.isClickable = true
    }

    private fun makeMarkerInvisible(markerListLoc1: MutableList<Marker>) {
        for (i in markerListLoc1) {
            i.isVisible = false
        }
    }

    private fun makeMarkerVisible(markerListLoc1: MutableList<Marker>) {
        for (i in markerListLoc1) {
            i.isVisible = true
        }
    }

    fun centroid(points: MutableList<LatLng>): LatLng {
        val centroid = doubleArrayOf(0.0, 0.0)
        for (i in points) {
            centroid[0] += i.latitude
            centroid[1] += i.longitude
        }
        val totalPoints = points.size
        centroid[0] = centroid[0] / totalPoints
        centroid[1] = centroid[1] / totalPoints
        val center = LatLng(centroid[0], centroid[1])
        return center
    }

    fun addPolygonToMap(
        myPolygons: PolygonModelClass,
        googleMap: GoogleMap,
        polygonList: MutableList<Polygon>,
        zone: String,
        polyList: MutableList<LatLng>,
        color: String,
        strokeColor: String,
        markerConfig: MarkerConfig = MarkerConfig()
    ) {
        myPolygons.features?.filter { it.attributes?.zone == zone }?.forEach { it ->
            it.geometry?.rings?.elementAt(0)?.forEach {
                polyList.add(LatLng(it.elementAt(1) - 0.00002, it.elementAt(0) + 0.000028))
            }
        }
        val addingPolygons = googleMap.addPolygon(
            PolygonOptions()
                .addAll(polyList)
                .fillColor(Color.parseColor(color))
                .visible(true)
                .strokeColor(Color.parseColor(strokeColor))
                .strokeWidth(5F)
                .clickable(true)
        )

        myPolygons.features?.filter { it.attributes?.zone == zone }?.forEach { it ->
            for (i in it.attributes?.number?.split(",")!!) {
                zonesMap[i] = addingPolygons
                val addingMarkers = googleMap.addMarker(
                    MarkerOptions()
                        .position(centroid(polyList))
                        .title(i)
                        .icon(bitmapFromVector(applicationContext, R.drawable.ic_zone_sign, i, markerConfig))
                        .anchor(0.35f, 0.3f)
                        .visible(false)
                )

                when (i.toInt()) {
                    in 504..509 -> addingMarkers?.let { it1 -> markerListZone0.add(it1) }
                    in 511..516 -> addingMarkers?.let { it1 -> markerListZone1.add(it1) }
                    in 518..523 -> addingMarkers?.let { it1 -> markerListZone2.add(it1) }
                    in 525..530 -> addingMarkers?.let { it1 -> markerListZone3.add(it1) }
                    in 532..536 -> addingMarkers?.let { it1 -> markerListZone4.add(it1) }
                    in 537..539 -> addingMarkers?.let { it1 -> markerListZone0.add(it1) }
                    540 -> addingMarkers?.let { it1 -> markerListZoneL1.add(it1) }
                    541 -> addingMarkers?.let { it1 -> markerListZoneL2.add(it1) }
                    542 -> addingMarkers?.let { it1 -> markerListZoneL3.add(it1) }
                    500, 543 -> addingMarkers?.let { it1 -> markerListZone1.add(it1) }
                    501, 502 -> addingMarkers?.let { it1 -> markerListZone0.add(it1) }
                }
                markerMap[i.toInt()] = listOf(
                    centroid(polyList).latitude + 0.00002,
                    centroid(polyList).longitude - 0.000035
                )
            }
        }
        polygonList.add(addingPolygons)
        makePolygonInvisible(addingPolygons)
    }

    fun addFloorPolygonToMap(
        myPolygons: PolygonModelClass,
        googleMap: GoogleMap,
        polygonList: MutableList<Polygon>,
        polyList: MutableList<LatLng>,
        color: String,
        strokeColor: String,
        markerConfig: MarkerConfig = MarkerConfig()
    ) {
        myPolygons.features?.forEach { it ->
            it.geometry?.rings?.elementAt(0)?.forEach {
                polyList.add(LatLng(it.elementAt(1) - 0.00002, it.elementAt(0) + 0.000028))
            }
        }

        val addingPolygons = googleMap.addPolygon(
            PolygonOptions()
                .addAll(polyList)
                .fillColor(Color.parseColor(color))
                .visible(true)
                .strokeColor(Color.parseColor(strokeColor))
                .strokeWidth(5F)
                .clickable(true)
        )

        myPolygons.features?.forEach { it ->
            for (i in it.attributes?.number?.split(",")!!) {
                zonesMap[i] = addingPolygons
                val addingMarkers = googleMap.addMarker(
                    MarkerOptions()
                        .position(centroid(polyList))
                        .title(i)
                        .icon(bitmapFromVector(applicationContext, R.drawable.ic_floor_sign, i,
                            markerConfig))
                        .anchor(0.35f, 0.3f)
                        .visible(false)
                )

                when (i.toInt()) {
                    503 -> addingMarkers?.let { it1 ->
                        markerListFloor0.add(it1)
                    }
                    510 -> addingMarkers?.let { it1 ->
                        markerListFloor1.add(it1)
                    }
                    517 -> addingMarkers?.let { it1 ->
                        markerListFloor2.add(it1)
                    }
                    524 -> addingMarkers?.let { it1 ->
                        markerListFloor3.add(it1)
                    }
                    531 -> addingMarkers?.let { it1 ->
                        markerListFloor4.add(it1)
                    }

                }
                markerMap[i.toInt()] = listOf(
                    centroid(polyList).latitude + 0.00002,
                    centroid(polyList).longitude - 0.000035
                )
            }
        }
        polygonList.add(addingPolygons)
        makePolygonInvisible(addingPolygons)
    }

    private fun addMarkersToMap(
        locationArray: MutableMap<Int, List<Double>>,
        googleMap: GoogleMap,
        markerList: MutableList<Marker>, shape: Int,
        markerConfig: MarkerConfig = MarkerConfig()
    ) {
        for (i in locationArray.entries.iterator()) {
            val point = LatLng(i.value.elementAt(0) - 0.00002, i.value.elementAt(1) + 0.000035)
            //Dodane przesunięcie żeby lepiej zgrać punktu z podkładem
            val addingMarkers = googleMap.addMarker(
                MarkerOptions()
                    .position(point)
                    .title(i.key.toString())
//                    .snippet(i.key.toString())
                    .icon(
                        bitmapFromVector(
                            applicationContext,
                            shape,
                            i.key.toString(),
                            markerConfig
                        )
                    )
                    .anchor(0.5f, 0.5f)
            )
            if (addingMarkers != null) {
                markerList.add(addingMarkers)
            }
        }
        makeMarkerInvisible(markerList)
    }


    private fun sortingMarkers(
        myMarkers: MarkersModelClass,
        idFirst: Int,
        idLast: Int,
        floor: Int,
        locationArray1: MutableMap<Int, List<Double>>,
        googleMap: GoogleMap,
        markerListLoc1: MutableList<Marker>,
        shape: Int,
        markerConfig: MarkerConfig = MarkerConfig()
    ) {
        myMarkers.features?.filter { it.properties?.IDP!! in idFirst until idLast && it.properties?.FLOOR == floor }
            ?.forEach {

//                println("ttt ${it.properties?.IDP}")
                val coordLong = it.geometry?.coordinates?.elementAt(0)?.toDouble() ?: 0.0
                val coordLat = it.geometry?.coordinates?.elementAt(1)?.toDouble() ?: 0.0
                val pointId = it.properties?.IDP ?: 0
                val floorNr = it.properties?.FLOOR?.toDouble() ?: 0.0

                locationArray1[pointId] = listOf(coordLat, coordLong)
                markerMap[pointId] = listOf(coordLat, coordLong, floorNr)
            }
        addMarkersToMap(locationArray1, googleMap, markerListLoc1, shape, markerConfig)
    }

    private fun sortingRoomsNumbers(
        myMarkers: RoomsModelClass,
//        locationArray1: MutableMap<String, List<Double>>,
    ) {
        myMarkers.features?.forEach {

//                println("ttt ${it.properties?.IDP}")
                val coordLong = it.geometry?.coordinates?.elementAt(0)?.toDouble() ?: 0.0
                val coordLat = it.geometry?.coordinates?.elementAt(1)?.toDouble() ?: 0.0
                val pointId = it.properties?.SHORTNAME ?: "null"
                val floorNr = it.properties?.FLOOR_ID?.toDouble() ?: 0.0

//                locationArray1[pointId] = listOf(coordLat, coordLong)
            roomMap[pointId] = listOf(coordLat, coordLong, floorNr)
            }
    }


    private fun setGroundOverlay(overlay: Int) {
        val newarkLatLng = LatLng(52.220550, 21.011190)//współrzędne wstawienia
        val newarkMap = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(overlay))
            .position(newarkLatLng, 270f)
        imageOverlay = mMap?.addGroundOverlay(newarkMap)!!
    }

    private fun replaceFragment(fragment: Fragment, anim1: Int, anim2: Int, container: Int) {

        hideFragment(R.id.fragmentContainerMenu, R.anim.enter_from_left, R.anim.exit_to_left)
//        hideBottomPanel(R.id.ContainerBottomPanel)

        val fragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(anim1, anim2)
        transaction.replace(container, fragment)
        transaction.commit()
    }

    fun hideFragment(container: Int, anim1: Int, anim2: Int) {
        var frag = supportFragmentManager.findFragmentById(container)
        if (frag != null) {
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.setCustomAnimations(anim1, anim2)
            transaction.remove(frag)
            transaction.commit()
        }
    }

    fun hideBottomPanel(container: Int) {
        var frag = supportFragmentManager.findFragmentById(container)
        if (frag != null) {
            ttsHelper?.stopSpeaking()
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
            transaction.remove(frag)
            transaction.commit()

            bottomButton.visibility = View.VISIBLE


            val view: View? = this.currentFocus
            if (view != null) {
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            this.window.decorView.rootView.announceForAccessibility("Zamknięto panel do odczytywania komunikatów.")

            if (clickedPanel) {
                val animation: Animation = TranslateAnimation(0F, 0F, -750F, 0F)
                animation.duration = 500
                animation.fillAfter = true
                commentBtn.startAnimation(animation)
                commentBtn.y = commentBtn.y + 750F
                scannerBtn.startAnimation(animation)
                scannerBtn.y = scannerBtn.y + 750F
            }

            clickedPanel = false
        }
    }


    private fun copyDatabase(dbName: String) {
        //get context by calling "this" in activity or getActivity() in fragment
        //call this if API level is lower than 17  String appDataPath = "/data/data/" + context.getPackageName() + "/databases/"
        val appDataPath: String = this.applicationInfo.dataDir
        val dbFolder = File("$appDataPath/databases") //Make sure the /databases folder exists
        dbFolder.mkdir() //This can be called multiple times.
        val dbFilePath = File("$appDataPath/databases/$dbName")
        try {
            val inputStream: InputStream = this.assets.open(dbName)
            val outputStream: OutputStream = FileOutputStream(dbFilePath)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            //handle
        }
    }

    private fun readJson(file: String): String {
        val json: String?
        val inputStream: InputStream = assets.open(file)
        json = inputStream.bufferedReader().use { it.readText() }
        return json
    }

    private fun readJsonPoly(): String {
        val json: String?
        val inputStream: InputStream = assets.open("strefy_all.json")
        json = inputStream.bufferedReader().use { it.readText() }
        return json
    }

    @SuppressLint("ResourceAsColor")
    private fun bitmapFromVector(
        context: Context,
        vectorResId: Int,
        numer: String,
        markerConfig: MarkerConfig = MarkerConfig()
    ): BitmapDescriptor {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
            ?: throw NullPointerException("Cannot get drawable for number $numer")
        // below line is use to set bounds to our vector drawable.
        val width = vectorDrawable.intrinsicWidth * markerSizeFactor
        val height = vectorDrawable.intrinsicHeight * markerSizeFactor
        vectorDrawable.setBounds(0, 0, width, height)
        // below line is use to create a bitmap for our drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.parseColor("#E2FFDF")
        //Rozmiar testu na markerze
        paint.textSize = markerConfig.textSize

        // below line is use to draw our vector drawable in canvas.
        vectorDrawable.draw(canvas)

        //Przesunięcie i rozmiar teksu na markerze w zależności od długości numeru i kształtu markera
        if ((numer.toInt() in 100..199) || numer.toInt() >= 300) {
            paint.textSize = markerConfig.textSize - markerConfig.threeDigitsNumTextSizeOffset
            canvas.drawText(
                numer,
                markerConfig.threeDigitsNumTextPositionX,
                markerConfig.threeDigitsNumTextPositionY,
                paint
            )
        } else if (numer.toInt() in 10..99) {
            canvas.drawText(
                numer,
                markerConfig.twoDigitsNumTextPositionX,
                markerConfig.twoDigitsNumTextPositionY,
                paint
            )
        } else if (numer.toInt() < 10) {
            canvas.drawText(
                numer,
                markerConfig.oneDigitsNumTextPositionX,
                markerConfig.oneDigitsNumTextPositionY,
                paint
            )
        } else if (numer.toInt() in 200..299) {
            paint.textSize = markerConfig.textSize - markerConfig.warningTextSizeOffset
            paint.color = Color.parseColor("#353536")
            canvas.drawText(
                numer,
                markerConfig.warningTextPositionX,
                markerConfig.warningTextPositionY,
                paint
            )
        }

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
////////////////////////////////////////////////////////////////////////////////////////////////DO ZROBIENIA

    private fun clickFloor(floorBtn: Button) {
        when (floorVisibility) {
            0 -> switchVisibilityFloor(markerListFloor0, allFloorPolygons, floorBtn)
            1 -> switchVisibilityFloor(markerListFloor1, allFloorPolygons, floorBtn)
            2 -> switchVisibilityFloor(markerListFloor2, allFloorPolygons, floorBtn)
            3 -> switchVisibilityFloor(markerListFloor3, allFloorPolygons, floorBtn)
            4 -> switchVisibilityFloor(markerListFloor4, allFloorPolygons, floorBtn)
//            5 -> switchVisibilityPoly(markerListFloorL1, allPolygonsLibrary, floorBtn)
//            6 -> switchVisibilityPoly(markerListFloorL2, allPolygonsLibrary, floorBtn)
//            7 -> switchVisibilityPoly(markerListFloorL3, allPolygonsLibrary, floorBtn)
        }
    }

    private fun clickPoly(polyBtn: Button) {
        when (floorVisibility) {
            0 -> switchVisibilityPoly(
                markerListZone0,
                (allPolygonsAtFloor.plus(allPolygonsOutsideOnGround)
                    .plus(allPolygonsLibrary) as MutableList<Polygon>),
                polyBtn
            )
            1 -> switchVisibilityPoly(
                markerListZone1,
                (allPolygonsAtFloor.plus(allPolygonsOutsideOnFront)
                    .plus(allPolygonsLibrary) as MutableList<Polygon>),
                polyBtn
            )
            2 -> switchVisibilityPoly(
                markerListZone2,
                allPolygonsAtFloor.plus(allPolygonsLibrary) as MutableList<Polygon>,
                polyBtn
            )
            3 -> switchVisibilityPoly(
                markerListZone3,
                allPolygonsAtFloor.plus(allPolygonsLibrary) as MutableList<Polygon>,
                polyBtn
            )
            4 -> switchVisibilityPoly(markerListZone4, allPolygonsAtFloor, polyBtn)
            5 -> switchVisibilityPoly(markerListZoneL1, allPolygonsLibrary, polyBtn)
            6 -> switchVisibilityPoly(markerListZoneL2, allPolygonsLibrary, polyBtn)
            7 -> switchVisibilityPoly(markerListZoneL3, allPolygonsLibrary, polyBtn)
        }
    }

    //W zależności od wybranego piętra dostosowywane jest wyświetlanie markerów
    private fun clickLoc(locBtn: Button) {
        if (clickedLoc) {
            locBtn.setBackgroundResource(R.drawable.marker_invis_btn)
        } else {
            locBtn.setBackgroundResource(R.drawable.marker_vis_btn)
        }
        toggleLocationMarkersVisibility()
    }

    private fun toggleLocationMarkersVisibility() {
        when (floorVisibility) {
            0 -> switchLocMarkersVisibility(markerListLoc0)
            1 -> switchLocMarkersVisibility(markerListLoc1)
            2 -> switchLocMarkersVisibility(markerListLoc2)
            3 -> switchLocMarkersVisibility(markerListLoc3)
            4 -> switchLocMarkersVisibility(markerListLoc4)
            5 -> switchLocMarkersVisibility(markerListLocL1)
            6 -> switchLocMarkersVisibility(markerListLocL2)
            7 -> switchLocMarkersVisibility(markerListLocL3)
        }
    }

    private fun toggleWarningMarkersVisibility() {
        when (floorVisibility) {
            0 -> switchVisibilityWar(markerListWar0)
            1 -> switchVisibilityWar(markerListWar1)
            2 -> switchVisibilityWar(markerListWar2)
            3 -> switchVisibilityWar(markerListWar3)
            4 -> switchVisibilityWar(markerListWar4)
            5 -> switchVisibilityWar(markerListWarL1)
            6 -> switchVisibilityWar(markerListWarL2)
            7 -> switchVisibilityWar(markerListWarL3)
        }
    }

    private fun togglePoiMarkersVisibility() {
        when (floorVisibility) {
            0 -> switchVisibilityPoi(markerListPoi0)
            1 -> switchVisibilityPoi(markerListPoi1)
            2 -> switchVisibilityPoi(markerListPoi2)
            3 -> switchVisibilityPoi(markerListPoi3)
            4 -> switchVisibilityPoi(markerListPoi4)
            5 -> switchVisibilityPoi(markerListPoiL1)
            6 -> switchVisibilityPoi(markerListPoiL2)
            7 -> switchVisibilityPoi(markerListPoiL3)
        }
    }

    private fun togglePolyMarkersVisibility() {
        when (floorVisibility) {
            0 -> switchVisibilityPoly(
                markerListZone0,
                (allPolygonsAtFloor.plus(allPolygonsOutsideOnGround)
                    .plus(allPolygonsLibrary) as MutableList<Polygon>),
                polyButton
            )
            1 -> switchVisibilityPoly(
                markerListZone1,
                (allPolygonsAtFloor.plus(allPolygonsOutsideOnFront)
                    .plus(allPolygonsLibrary) as MutableList<Polygon>),
                polyButton
            )
            2 -> switchVisibilityPoly(
                markerListZone2,
                allPolygonsAtFloor.plus(allPolygonsLibrary) as MutableList<Polygon>,
                polyButton
            )
            3 -> switchVisibilityPoly(
                markerListZone3,
                allPolygonsAtFloor.plus(allPolygonsLibrary) as MutableList<Polygon>,
                polyButton
            )
            4 -> switchVisibilityPoly(markerListZone4, allPolygonsAtFloor, polyButton)
            5 -> switchVisibilityPoly(markerListZoneL1, allPolygonsLibrary, polyButton)
            6 -> switchVisibilityPoly(markerListZoneL2, allPolygonsLibrary, polyButton)
            7 -> switchVisibilityPoly(markerListZoneL3, allPolygonsLibrary, polyButton)

        }
    }
    private fun toggleFloorMarkersVisibility() {
        when (floorVisibility) {
            0 -> switchVisibilityFloor(markerListFloor0, allFloorPolygons, floorButton)
            1 -> switchVisibilityFloor(markerListFloor1, allFloorPolygons, floorButton)
            2 -> switchVisibilityFloor(markerListFloor2, allFloorPolygons, floorButton)
            3 -> switchVisibilityFloor(markerListFloor3, allFloorPolygons, floorButton)
            4 -> switchVisibilityFloor(markerListFloor4, allFloorPolygons, floorButton)

        }
    }

    private fun switchVisibilityFloor(
        markerList: MutableList<Marker>,
        allPolygon: MutableList<Polygon>,
        btn: Button
    ) {
        clickedFloor = if (!clickedFloor) {
            for (i in allPolygon) {
                makePolygonVisible(i)
            }
            for (j in markerList) {
                j.isVisible = true
            }
            btn.setBackgroundResource(R.drawable.marker_vis_btn)
            true
        } else {
            for (i in allPolygon) {
                makePolygonInvisible(i)
            }
            for (j in markerList) {
                j.isVisible = false
            }
            btn.setBackgroundResource(R.drawable.marker_invis_btn)
            false
        }
    }

    private fun switchVisibilityPoly(
        markerList: MutableList<Marker>,
        allPolygon: MutableList<Polygon>,
        btn: Button
    ) {
        clickedPoly = if (!clickedPoly) {
            for (i in allPolygon) {
                makePolygonVisible(i)
            }
            for (j in markerList) {
                j.isVisible = true
            }
            btn.setBackgroundResource(R.drawable.marker_vis_btn)
            true
        } else {
            for (i in allPolygon) {
                makePolygonInvisible(i)
            }
            for (j in markerList) {
                j.isVisible = false
            }
            btn.setBackgroundResource(R.drawable.marker_invis_btn)
            false
        }
    }

    private fun switchLocMarkersVisibility(markerList: MutableList<Marker>) {
        clickedLoc = if (!clickedLoc) {
            makeMarkerVisible(markerList)
            true
        } else {
            makeMarkerInvisible(markerList)
            false
        }
    }

    private fun switchVisibilityWar(markerList: MutableList<Marker>) {
        clickedWar = if (!clickedWar) {
            makeMarkerVisible(markerList)
            true
        } else {
            makeMarkerInvisible(markerList)
            false
        }
    }

    private fun switchVisibilityPoi(markerList: MutableList<Marker>) {
        clickedPoi = if (!clickedPoi) {
            makeMarkerVisible(markerList)
            true
        } else {
            makeMarkerInvisible(markerList)
            false
        }
    }

    private fun openBottomPanel(
        bottomButton: Button,
        floorNumber: TextView,
        db: Database,
        btn: Button,
        setBtn: Button
    ) {

//        if(!clickedPanel){
        val animation: Animation = TranslateAnimation(0F, 0F, 750F, 0F)
        animation.duration = 500
        animation.fillAfter = true
        btn.startAnimation(animation)
        btn.y = btn.y - 750F

        setBtn.startAnimation(animation)
        setBtn.y = setBtn.y - 750F
//        }


        //Przycisk znika z opóźnieniem, żeby wyglądało lepiej wizualnie przy pojawiającym się
        //dolnym panelu (?mogłoby być rozwiązane gdyby można było przeciągnąć panel zamiat klikać)
        Handler().postDelayed({
            bottomButton.visibility = View.INVISIBLE
        }, 300)
        replaceFragment(
            FragmentBottomPanel(bottomButton, floorNumber, db, btn),
            R.anim.enter_from_bottom,
            R.anim.exit_to_bottom,
            R.id.ContainerBottomPanel
        )
        clickedPanel = true
    }


    private fun GoogleMap.setMapClickListener () {
        setOnMapClickListener {
            currentFocus?.windowToken?.let {
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(it, 0)
            }
        }
    }



}

//fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Bitmap? {
//    val width = bm.width
//    val height = bm.height
//    val scaleWidth = newWidth.toFloat() / width
//    val scaleHeight = newHeight.toFloat() / height
//    val matrix = Matrix()
//    matrix.postScale(scaleWidth, scaleHeight)
//    return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
////    return Bitmap.createScaledBitmap(bm, width, height, false)
//}


