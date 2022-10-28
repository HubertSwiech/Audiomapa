package com.example.gg_dyplom
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.gg_dyplom.databinding.FragmentMenuBinding
import com.google.android.gms.maps.model.*

class FragmentMenu(floor: TextView) : Fragment(R.layout.fragment_menu){

    lateinit var binding: FragmentMenuBinding
    lateinit var ACTIVITY: MapsActivity
    var floorTextView = floor
    lateinit var btn0: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ACTIVITY = context as MapsActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMenuBinding.inflate(layoutInflater)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Handler().postDelayed({
            btn0.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }, 2000)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {

        val v = inflater.inflate(R.layout.fragment_menu, container, false )

        val imgOvrly = ACTIVITY.imageOverlay

//        v.setOnTouchListener { v, event ->
//            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
//            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
//            transaction.hide(this)
//            transaction.commit()
//            v?.onTouchEvent(event) ?: true
//        }

        btn0 = v.findViewById<Button>(R.id.fragment0Btn)
        btn0.setOnClickListener{
            buttonActions(imgOvrly, 0, "0", R.drawable.pietro0)
        }

        val btn1 = v.findViewById<Button>(R.id.fragment1Btn)
        btn1.setOnClickListener{
            buttonActions(imgOvrly, 1, "1", R.drawable.pietro1)
        }

        val btn2 = v.findViewById<Button>(R.id.fragment2Btn)
        btn2.setOnClickListener{
            buttonActions(imgOvrly, 2, "2", R.drawable.pietro2)
        }

        val btn3 = v.findViewById<Button>(R.id.fragment3Btn)
        btn3.setOnClickListener{
            buttonActions(imgOvrly, 3, "3", R.drawable.pietro3)
        }

        val btn4 = v.findViewById<Button>(R.id.fragment4Btn)
        btn4.setOnClickListener{
            buttonActions(imgOvrly, 4, "4", R.drawable.pietro4)
        }

        val btnL1 = v.findViewById<Button>(R.id.fragmentL1Btn)
        btnL1.setOnClickListener{
            buttonActions(imgOvrly, 5, "L1", R.drawable.pietrol1)
        }

        val btnL2 = v.findViewById<Button>(R.id.fragmentL2Btn)
        btnL2.setOnClickListener{
            buttonActions(imgOvrly, 6, "L2", R.drawable.pietrol2)
        }

        val btnL3 = v.findViewById<Button>(R.id.fragmentL3Btn)
        btnL3.setOnClickListener{
            buttonActions(imgOvrly, 7, "L3", R.drawable.pietrol3)
        }

        val btnStrefy = v.findViewById<Button>(R.id.fragmentStrefyBtn)
        btnStrefy.setOnClickListener{
            hideFragment()
            imgOvrly.remove()
            replaceOverlay(R.drawable.strefyovr)
            ACTIVITY.floorVisibility = 8
            hideAllMarkers()
            floorTextView.text = ""
            this.view?.announceForAccessibility("Zamknięto listwę z numerami pięter. Wyświetlono mapę piętra z widokiem stref")
        }

        return v
    }

    private fun buttonActions(img: GroundOverlay, floor: Int, floorVis: String, pietro: Int){
        hideFragment()
        img.remove()
        replaceOverlay(pietro)
        ACTIVITY.floorVisibility = floor
        changeLoc()
        changeWar()
        changePoi()
        changePoly()
        changeFloor(ACTIVITY)
        floorTextView.text = floorVis
        this.view?.announceForAccessibility("Zamknięto listwę z numerami pięter. Wyświetlono mapę piętra o numerze $floorVis")
    }

//powtórka
    private fun hideFragment() {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        transaction.remove(this)
        transaction.commit()
    }

    private fun  replaceOverlay(overlay: Int){
        val newarkLatLng = LatLng(52.220550, 21.011190)
        val newarkMap = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(overlay))
            .position(newarkLatLng, 270f)
        ACTIVITY.imageOverlay = ACTIVITY.mMap.addGroundOverlay(newarkMap)!!
    }

    private fun makeMarkerInvisible(markerListLoc1: MutableList<Marker>){
        for(i in markerListLoc1){
            i.isVisible = false
        }
    }

    private fun makeMarkerVisible(markerListLoc1: MutableList<Marker>){
        for(i in markerListLoc1){
            i.isVisible = true
        }
    }

    private fun changeLoc(){
        if(ACTIVITY.clickedLoc){
            for(i in ACTIVITY.markerLocListArray){
                makeMarkerInvisible(i)
            }
            makeMarkerVisible(ACTIVITY.markerLocListArray[ACTIVITY.floorVisibility])
        }
    }

    private fun changeWar(){
        if(ACTIVITY.clickedWar){
            for(i in ACTIVITY.markerWarListArray){
                makeMarkerInvisible(i)
            }
            makeMarkerVisible(ACTIVITY.markerWarListArray[ACTIVITY.floorVisibility])
        }
    }

    private fun changePoi(){
        if(ACTIVITY.clickedPoi){
            for(i in ACTIVITY.markerPoiListArray){
                makeMarkerInvisible(i)
            }
            makeMarkerVisible(ACTIVITY.markerPoiListArray[ACTIVITY.floorVisibility])
        }
    }
    private fun makePolygonVisibleMenu(polygon: MutableList<Polygon>){
        for(i in polygon){
            i.isVisible = true
            i.isClickable = true
        }
        makeMarkerVisible(ACTIVITY.markerZoneListArray[ACTIVITY.floorVisibility])
    }
    private fun changePoly(){
        if(ACTIVITY.clickedPoly){
            for(i in ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsOutsideOnGround).plus(ACTIVITY.allPolygonsOutsideOnFront.plus(ACTIVITY.allPolygonsLibrary))){
                i.isVisible = false
                i.isClickable = false
            }
            for(i in ACTIVITY.markerZoneListArray){
                makeMarkerInvisible(i)
            }
            when (ACTIVITY.floorVisibility) {
                0 -> makePolygonVisibleMenu((ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsOutsideOnGround).plus(ACTIVITY.allPolygonsLibrary) as MutableList<Polygon>))
                1 -> makePolygonVisibleMenu((ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsOutsideOnFront).plus(ACTIVITY.allPolygonsLibrary) as MutableList<Polygon>))
                2 -> makePolygonVisibleMenu(ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsLibrary) as MutableList<Polygon>)
                3 -> makePolygonVisibleMenu(ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsLibrary) as MutableList<Polygon>)
                4 -> makePolygonVisibleMenu(ACTIVITY.allPolygonsAtFloor)
                5 -> makePolygonVisibleMenu(ACTIVITY.allPolygonsLibrary)
                6 -> makePolygonVisibleMenu(ACTIVITY.allPolygonsLibrary)
                7 -> makePolygonVisibleMenu(ACTIVITY.allPolygonsLibrary)
            }
        }
    }

    fun changeFloor(ACTIVITY: MapsActivity){
        if(ACTIVITY.clickedFloor){
            for(i in ACTIVITY.allFloorPolygons){
                i.isVisible = false
                i.isClickable = false
            }
            for(i in ACTIVITY.markerFloorListArray){
                makeMarkerInvisible(i)
            }
            when (ACTIVITY.floorVisibility) {
                0 -> makeFloorPolygonVisibleMenu(ACTIVITY, ACTIVITY.allFloorPolygons)
                1 -> makeFloorPolygonVisibleMenu(ACTIVITY, ACTIVITY.allFloorPolygons)
                2 -> makeFloorPolygonVisibleMenu(ACTIVITY, ACTIVITY.allFloorPolygons)
                3 -> makeFloorPolygonVisibleMenu(ACTIVITY, ACTIVITY.allFloorPolygons)
                4 -> makeFloorPolygonVisibleMenu(ACTIVITY, ACTIVITY.allFloorPolygons)
//            5 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsLibrary)
//            6 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsLibrary)
//            7 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsLibrary)
            }
        }
    }

    private fun hideAllMarkers(){
        for(i in ACTIVITY.markerPoiListArray){
            makeMarkerInvisible(i)
        }
        for(i in ACTIVITY.markerLocListArray){
            makeMarkerInvisible(i)
        }
        for(i in ACTIVITY.markerWarListArray){
            makeMarkerInvisible(i)
        }
        ACTIVITY.clickedPoi = false
        ACTIVITY.clickedLoc = false
        ACTIVITY.clickedWar = false

        ACTIVITY.locButton.setBackgroundResource(R.drawable.marker_invis_btn)
        ACTIVITY.warButton.setBackgroundResource(R.drawable.marker_invis_btn)
        ACTIVITY.poiButton.setBackgroundResource(R.drawable.marker_invis_btn)
    }

}


