package com.example.gg_dyplom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Editable
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import java.util.*


class TtsHelper(context: Context, activity: MapsActivity) {

    var mTTS: TextToSpeech? = null
    var ACTIVITY = activity

    init {
        mTTS = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                //if there is no error then set language
                mTTS?.setSpeechRate(0.83F)
            }
        }
    }

    fun ttsLocation(
        mContext: Context,
        nrEditText: EditText,
        speakButton: Button,
        database: Database,
        fragManager: FragmentManager,
        komEditText: EditText,
        stopButton: Button,
        ACTIVITY: MapsActivity,
        floorTextView: TextView
    ){
//        val l = Locale("pl")

        speakButton.setOnClickListener {
            val toSpeak: String
            if (mTTS?.isSpeaking == true) {
                //if speaking then stop
                mTTS?.stop()
                //mTTS.shutdown()
            }

            val number = nrEditText.text.toString()

            if (!isNumber(number)) {
                val blad = "Sprawdź, czy numer jest wpisany prawidłowo."
                Toast.makeText(mContext, blad, Toast.LENGTH_SHORT).show()
            } else {
                //Przełączanie pietra
                switchFloorOverlay(number, ACTIVITY, floorTextView)
                drawCircle(number, ACTIVITY)

                database.open()
                val message = database.getLocation(number)
                database.close()

                toSpeak = message
                komEditText.text = toSpeak.toEditable()

                if (toSpeak == "") {
                    //if there is no text in edit text
                    Toast.makeText(mContext, "Wpisz numer lub wybierz punkt.", Toast.LENGTH_SHORT).show()
                } else {
                    //if there is text in edit text
                    mTTS?.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }

        stopButton.setOnClickListener {
            if (mTTS?.isSpeaking == true){
                //if speaking then stop
                mTTS?.stop()
                //mTTS.shutdown()
            }
            else{
                //if not speaking
//                Toast.makeText(mContext, "Not speaking", Toast.LENGTH_SHORT).show()
            }
        }
    }





        fun ttsNavigation(
            mContext: Context,
            startEditText: EditText,
            endEditText: Spinner,
            speakButton: Button,
            database: Database,
            fragManager: FragmentManager,
            komEditText: EditText,
            stopButton: Button,
            ACTIVITY: MapsActivity,
            floorTextView: TextView
        ){
//        val l = Locale("pl")

            speakButton.setOnClickListener {
                val toSpeak: String
                if (mTTS?.isSpeaking == true) {
                    //if speaking then stop
                    mTTS?.stop()
                    //mTTS.shutdown()
                }

                val number1 = startEditText.text.toString()
                val number2 = endEditText.selectedItem.toString()
                ACTIVITY.targetNumber = number2

                if(!isNumber(number1) || number1.toInt() > 149 || !isNumber(number2) || number2.toInt() > 149){
                    val blad = "Sprawdź, czy numery są wpisane prawidłowo."
                    Toast.makeText(mContext, blad, Toast.LENGTH_SHORT).show()
                } else {
                    //Przełączanie pietra
                    switchFloorOverlay(number1, ACTIVITY, floorTextView)
                    drawCircle(number1, ACTIVITY)
                    drawLine(number1, number2, ACTIVITY)

                    database.open()
                    val message = database.getNavigation(number1, number2)
                    database.close()

                    toSpeak = message
                    komEditText.text = toSpeak.toEditable()

                    if (toSpeak == "") {
                        //if there is no text in edit text
                        Toast.makeText(mContext, "Wpisz numer lub wybierz punkt.", Toast.LENGTH_SHORT).show()
                    } else {
                        //if there is text in edit text
                        mTTS?.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
                    }
                }
            }

        stopButton.setOnClickListener {
            if (mTTS?.isSpeaking == true){
                //if speaking then stop
                mTTS?.stop()
                //mTTS.shutdown()
            }
            else{
                //if not speaking
//                Toast.makeText(mContext, "Not speaking", Toast.LENGTH_SHORT).show()
            }
        }
    }

        fun ttsComment(mContext: Context, idx: Int, speakButton: Button, databaseCom: DatabaseCom, stopButton: Button
        ){
//        val l = Locale("pl")

            speakButton.setOnClickListener {
                val toSpeak: String
                speakButton.isPressed = true
                if (mTTS?.isSpeaking == true) {
                    //if speaking then stop
                    mTTS?.stop()
                    //mTTS.shutdown()
                }

                databaseCom.open()
                    val message = databaseCom.readComment(idx)
                    databaseCom.close()

                    toSpeak = message


                    if (toSpeak == "") {
                        //if there is no text in edit text
//                        Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show()
                    } else {
                        //if there is text in edit text
//                        Toast.makeText(mContext, toSpeak, Toast.LENGTH_SHORT).show()
                        val map = HashMap<String, String>()
                        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "UniqueID";
                        mTTS?.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map)
                    }

                mTTS?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        speakButton.isPressed = true
                    }
                    override fun onDone(utteranceId: String) {
                        speakButton.isPressed = false
                    }
                    override fun onError(utteranceId: String) {
                    }
                })

            }

                stopButton.setOnClickListener {
                    if (mTTS?.isSpeaking == true){
                        //if speaking then stop
                        mTTS?.stop()
                        speakButton.setPressed(false)
                        //mTTS.shutdown()
                    }
                    else{
                        //if not speaking
//                Toast.makeText(mContext, "Not speaking", Toast.LENGTH_SHORT).show()
                    }
                }



        }


    fun stopSpeaking() {
        if (mTTS?.isSpeaking == true){
            //if speaking then stop
            mTTS?.stop()
            //mTTS.shutdown()
        }
    }

}



//powtórki v
fun makeMarkerInvisible(markerListLoc1: MutableList<Marker>){
    for(i in markerListLoc1){
        i.isVisible = false
    }
}

fun makeMarkerVisible(markerListLoc1: MutableList<Marker>){
    for(i in markerListLoc1){
        i.isVisible = true
    }
}

fun changeLoc(ACTIVITY: MapsActivity){
    if(ACTIVITY.clickedLoc){
        for(i in ACTIVITY.markerLocListArray){
            makeMarkerInvisible(i)
        }
        makeMarkerVisible(ACTIVITY.markerLocListArray[ACTIVITY.floorVisibility])
    } else {
        for(i in ACTIVITY.markerLocListArray){
            makeMarkerInvisible(i)
        }
    }
}

fun changeWar(ACTIVITY: MapsActivity){
    if(ACTIVITY.clickedWar){
        for(i in ACTIVITY.markerWarListArray){
            makeMarkerInvisible(i)
        }
        makeMarkerVisible(ACTIVITY.markerWarListArray[ACTIVITY.floorVisibility])
    } else {
        for(i in ACTIVITY.markerWarListArray){
            makeMarkerInvisible(i)
        }
    }
}

fun changePoi(ACTIVITY: MapsActivity){
    if(ACTIVITY.clickedPoi){
        for(i in ACTIVITY.markerPoiListArray){
            makeMarkerInvisible(i)
        }
        makeMarkerVisible(ACTIVITY.markerPoiListArray[ACTIVITY.floorVisibility])
    } else {
        for(i in ACTIVITY.markerPoiListArray){
            makeMarkerInvisible(i)
        }
    }
}

fun makePolygonVisibleMenu(ACTIVITY: MapsActivity, polygon: MutableList<Polygon>){
    for(i in polygon){
        i.isVisible = true
        i.isClickable = true
    }
    makeMarkerVisible(ACTIVITY.markerZoneListArray[ACTIVITY.floorVisibility])
}

fun makeFloorPolygonVisibleMenu(ACTIVITY: MapsActivity, polygon: MutableList<Polygon>){
    for(i in polygon){
        i.isVisible = true
        i.isClickable = true
    }
    makeMarkerVisible(ACTIVITY.markerFloorListArray[ACTIVITY.floorVisibility])
}
fun changePoly(ACTIVITY: MapsActivity){
    if(ACTIVITY.clickedPoly){
        for(i in ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsOutsideOnGround).plus(ACTIVITY.allPolygonsOutsideOnFront).plus(ACTIVITY.allPolygonsLibrary)){
            i.isVisible = false
            i.isClickable = false
        }
        for(i in ACTIVITY.markerZoneListArray){
            makeMarkerInvisible(i)
        }
        when (ACTIVITY.floorVisibility) {
            0 -> makePolygonVisibleMenu(ACTIVITY, (ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsOutsideOnGround).plus(ACTIVITY.allPolygonsLibrary) as MutableList<Polygon>))
            1 -> makePolygonVisibleMenu(ACTIVITY, (ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsOutsideOnFront).plus(ACTIVITY.allPolygonsLibrary) as MutableList<Polygon>))
            2 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsLibrary) as MutableList<Polygon>)
            3 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsLibrary) as MutableList<Polygon>)
            4 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsAtFloor)
            5 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsLibrary)
            6 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsLibrary)
            7 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsLibrary)
        }
    } else {
        for(i in ACTIVITY.allPolygonsAtFloor.plus(ACTIVITY.allPolygonsOutsideOnGround).plus(ACTIVITY.allPolygonsOutsideOnFront).plus(ACTIVITY.allPolygonsLibrary)){
            i.isVisible = false
        }
        for(i in ACTIVITY.markerZoneListArray){
            makeMarkerInvisible(i)
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
            5 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsLibrary)
            6 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsLibrary)
            7 -> makePolygonVisibleMenu(ACTIVITY, ACTIVITY.allPolygonsLibrary)
        }
    } else {
        for(i in ACTIVITY.allFloorPolygons){
            i.isVisible = false
        }
        for(i in ACTIVITY.markerFloorListArray){
            makeMarkerInvisible(i)
        }
    }
}

fun isNumber(s: String): Boolean {
    return try {
        s.toInt()
        true
    } catch (ex: NumberFormatException) {
        false
    }
}
//powtórki ^

fun switchFloorOverlay(stringNumber: String, ACTIVITY: MapsActivity, floorTextView: TextView) {

    var pointFloor = 1
    if(stringNumber.toInt() < 500){
        val pointMap = ACTIVITY.markerMap.getValue(stringNumber.toInt())
        pointFloor = pointMap[2].toInt()
    } else {
        when (stringNumber.toInt()) {
            in 503..509 -> pointFloor = 0
            in 510..516 -> pointFloor = 1
            in 517..523 -> pointFloor = 2
            in 524..530 -> pointFloor = 3
            in 531..536 -> pointFloor = 4
            in 537..539 -> pointFloor = 0
            540 -> pointFloor = 5
            541 -> pointFloor = 6
            542 -> pointFloor = 7
            500, 543 -> pointFloor = 1
            501, 502 -> pointFloor = 0
        }
    }


    var typeShort = ""

    when {
        stringNumber.toInt() < 200 -> {typeShort = "loc"}
        stringNumber.toInt() in 200..299 -> {typeShort = "war"}
        stringNumber.toInt() in 300..499 -> {typeShort = "poi"}
        stringNumber.toInt() == 503 -> {typeShort = "floor"}
        stringNumber.toInt() == 510 -> {typeShort = "floor"}
        stringNumber.toInt() == 517 -> {typeShort = "floor"}
        stringNumber.toInt() == 524 -> {typeShort = "floor"}
        stringNumber.toInt() == 531 -> {typeShort = "floor"}
        stringNumber.toInt() in 500..502  -> {typeShort = "poly"}
        stringNumber.toInt() in 504..509  -> {typeShort = "poly"}
        stringNumber.toInt() in 511..516  -> {typeShort = "poly"}
        stringNumber.toInt() in 518..523  -> {typeShort = "poly"}
        stringNumber.toInt() in 525..530  -> {typeShort = "poly"}
        stringNumber.toInt() > 531  -> {typeShort = "poly"}
    }
    println("aaaaaaaaa $typeShort")

    var floorNumber = ""
    floorNumber = when (pointFloor) {
        5 -> { "L1" }
        6 -> { "L2" }
        7 -> { "L3" }
        else -> { pointFloor.toString() }
    }

//    println("aaaaaaaa: " + pointFloor + " " + stringNumber.toInt() + " " + typeShort + " " + floorNumber)

    when (pointFloor) {
        0 -> replaceOverlay(ACTIVITY, typeShort, floorTextView, floorNumber, R.drawable.pietro0)
        1 -> replaceOverlay(ACTIVITY, typeShort, floorTextView, floorNumber, R.drawable.pietro1)
        2 -> replaceOverlay(ACTIVITY, typeShort, floorTextView, floorNumber, R.drawable.pietro2)
        3 -> replaceOverlay(ACTIVITY, typeShort, floorTextView, floorNumber, R.drawable.pietro3)
        4 -> replaceOverlay(ACTIVITY, typeShort, floorTextView, floorNumber, R.drawable.pietro4)
        5 -> replaceOverlay(ACTIVITY, typeShort, floorTextView, floorNumber, R.drawable.pietrol1)
        6 -> replaceOverlay(ACTIVITY, typeShort, floorTextView, floorNumber, R.drawable.pietrol2)
        7 -> replaceOverlay(ACTIVITY, typeShort, floorTextView, floorNumber, R.drawable.pietrol3)
    }

//    when (stringNumber.toInt()) {
//        in 0..35    -> replaceOverlay(ACTIVITY,"loc", floorTextView, "1",  R.drawable.pietro1)
//        in 36..73   -> replaceOverlay(ACTIVITY,"loc", floorTextView, "0",  R.drawable.pietro0)
//        in 74..98   -> replaceOverlay(ACTIVITY,"loc", floorTextView, "2",  R.drawable.pietro2)
//        in 99..122  -> replaceOverlay(ACTIVITY,"loc", floorTextView, "3",  R.drawable.pietro3)
//        in 123..143 -> replaceOverlay(ACTIVITY,"loc", floorTextView, "4",  R.drawable.pietro4)
//        144, 145, 426, 427  -> replaceOverlay(ACTIVITY,"loc", floorTextView, "L1", R.drawable.pietrol1)
//        146, 147, 150, 428, 425  -> replaceOverlay(ACTIVITY,"loc", floorTextView, "L2", R.drawable.pietrol2)
//        148, 149, 429  -> replaceOverlay(ACTIVITY,"loc", floorTextView, "L3", R.drawable.pietrol3)
//        in 200..210 -> replaceOverlay(ACTIVITY,"war", floorTextView, "0",  R.drawable.pietro0)
//        in 211..226 -> replaceOverlay(ACTIVITY,"war", floorTextView, "1",  R.drawable.pietro1)
//        in 227..235 -> replaceOverlay(ACTIVITY,"war", floorTextView, "2",  R.drawable.pietro2)
//        in 236..246, 271 -> replaceOverlay(ACTIVITY,"war", floorTextView, "3",  R.drawable.pietro3)
//        in 247..259 -> replaceOverlay(ACTIVITY,"war", floorTextView, "4",  R.drawable.pietro4)
//        in 260..263 -> replaceOverlay(ACTIVITY,"war", floorTextView, "L1", R.drawable.pietrol1)
//        in 264..267 -> replaceOverlay(ACTIVITY,"war", floorTextView, "L2", R.drawable.pietrol2)
//        in 268..270 -> replaceOverlay(ACTIVITY,"war", floorTextView, "L3", R.drawable.pietrol3)
//        in 300..324 -> replaceOverlay(ACTIVITY,"poi", floorTextView, "0",  R.drawable.pietro0)
//        in 325..361 -> replaceOverlay(ACTIVITY,"poi", floorTextView, "1",  R.drawable.pietro1)
//        in 362..381 -> replaceOverlay(ACTIVITY,"poi", floorTextView, "2",  R.drawable.pietro2)
//        in 382..405 -> replaceOverlay(ACTIVITY,"poi", floorTextView, "3",  R.drawable.pietro3)
//        in 406..422 -> replaceOverlay(ACTIVITY,"poi", floorTextView, "4",  R.drawable.pietro4)
//        423, 424    -> replaceOverlay(ACTIVITY,"poi", floorTextView, "0",  R.drawable.pietro0)
//        else -> {
//            print("brak numeru")
//        }
//    }
}


fun replaceOverlay(ACTIVITY: MapsActivity, pkt:String, floorTextView: TextView, nr: String, overlay: Int) {
    ACTIVITY.imageOverlay.remove()

    val newarkLatLng = LatLng(52.220550, 21.011190)
    val newarkMap = GroundOverlayOptions()
        .image(BitmapDescriptorFactory.fromResource(overlay))
        .position(newarkLatLng, 270f)

    ACTIVITY.mMap?.let {
        ACTIVITY.imageOverlay = it.addGroundOverlay(newarkMap)!!
    }

    when (nr) {
        "L1" -> { ACTIVITY.floorVisibility = 5 }
        "L2" -> { ACTIVITY.floorVisibility = 6 }
        "L3" -> { ACTIVITY.floorVisibility = 7 }
        else -> { ACTIVITY.floorVisibility = nr.toInt() }
    }


    floorTextView.text = nr
    when (pkt) {
        "loc" -> {
            ACTIVITY.clickedLoc = true
            ACTIVITY.clickedWar = false
            ACTIVITY.clickedPoi = false
            ACTIVITY.clickedPoly = false
            ACTIVITY.clickedFloor = false
            ACTIVITY.locButton.setBackgroundResource(R.drawable.marker_vis_btn)
            ACTIVITY.warButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.poiButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.polyButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.floorButton.setBackgroundResource(R.drawable.marker_invis_btn)
        }
        "war" -> {
            ACTIVITY.clickedLoc = false
            ACTIVITY.clickedWar = true
            ACTIVITY.clickedPoi = false
            ACTIVITY.clickedPoly = false
            ACTIVITY.clickedFloor = false
            ACTIVITY.locButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.warButton.setBackgroundResource(R.drawable.marker_vis_btn)
            ACTIVITY.poiButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.polyButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.floorButton.setBackgroundResource(R.drawable.marker_invis_btn)
        }
        "poi" -> {
            ACTIVITY.clickedLoc = false
            ACTIVITY.clickedWar = false
            ACTIVITY.clickedPoi = true
            ACTIVITY.clickedPoly = false
            ACTIVITY.clickedFloor = false
            ACTIVITY.locButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.warButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.poiButton.setBackgroundResource(R.drawable.marker_vis_btn)
            ACTIVITY.polyButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.floorButton.setBackgroundResource(R.drawable.marker_invis_btn)
        }
        "poly" -> {
            ACTIVITY.clickedLoc = false
            ACTIVITY.clickedWar = false
            ACTIVITY.clickedPoi = false
            ACTIVITY.clickedPoly = true
            ACTIVITY.clickedFloor = false
            ACTIVITY.locButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.warButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.poiButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.polyButton.setBackgroundResource(R.drawable.marker_vis_btn)
            ACTIVITY.floorButton.setBackgroundResource(R.drawable.marker_invis_btn)
        }
        "floor" -> {
            ACTIVITY.clickedLoc = false
            ACTIVITY.clickedWar = false
            ACTIVITY.clickedPoi = false
            ACTIVITY.clickedPoly = false
            ACTIVITY.clickedFloor = true
            ACTIVITY.locButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.warButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.poiButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.polyButton.setBackgroundResource(R.drawable.marker_invis_btn)
            ACTIVITY.floorButton.setBackgroundResource(R.drawable.marker_vis_btn)
        }
    }
    changeLoc(ACTIVITY)
    changeWar(ACTIVITY)
    changePoi(ACTIVITY)
    changePoly(ACTIVITY)
    changeFloor(ACTIVITY)
}


fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

fun drawCircle(number: String, ACTIVITY: MapsActivity) {
    if(ACTIVITY.mapCircle!=null){
        ACTIVITY.mapCircle.remove()
    }

    for(i in ACTIVITY.markerMap){
        if(i.key == number.toInt()){
            // Instantiating CircleOptions to draw a circle around the marker
            val circleOptions = CircleOptions()
            // Specifying the center of the circle
            circleOptions.center(LatLng(i.value[0]-0.00002, i.value[1]+0.000035))
            // Radius of the circle
            circleOptions.radius(5.0)
            // Border color of the circle
            circleOptions.strokeColor(0x70E50000)
            // Fill color of the circle
            circleOptions.fillColor(0x50ff0000)
            // Border width of the circle
            circleOptions.strokeWidth(50f)
            circleOptions.zIndex(2F)
            // Adding the circle to the GoogleMap
            ACTIVITY.mMap?.let {
                ACTIVITY.mapCircle = it.addCircle(circleOptions)
            }
            val cp = CameraPosition.Builder()
                .bearing(-65f)
                .target(LatLng(i.value[0]-0.00002, i.value[1]+0.000035))
                .zoom(20f)
                .build()
            val cu = CameraUpdateFactory.newCameraPosition(cp)
            ACTIVITY.mMap?.animateCamera(cu)
        }
    }


}

fun drawLine(nrStart: String, nrEnd: String, ACTIVITY: MapsActivity){
    if(ACTIVITY.mapLine!=null){ ACTIVITY.mapLine.remove() }
    if(ACTIVITY.lineMarker!=null){ ACTIVITY.lineMarker.remove() }

    val startValue = ACTIVITY.markerMap.getValue(nrStart.toInt())
    val endValue = ACTIVITY.markerMap.getValue(nrEnd.toInt())

    val DOT: PatternItem = Dot()
    val GAP: PatternItem = Gap(15F)
    val PATTERN_POLYGON_ALPHA: List<PatternItem> = Arrays.asList(DOT, GAP)

    val polyOptions = PolylineOptions()
    polyOptions
        .pattern(PATTERN_POLYGON_ALPHA)
        .color(Color.BLUE)
        .width(20F)
        .clickable(false)
        .add(
            LatLng(startValue[0]-0.00002, startValue[1]+0.000035),
            LatLng(endValue[0]-0.00002, endValue[1]+0.000035))

    ACTIVITY.mMap?.let {
        ACTIVITY.mapLine = it.addPolyline(polyOptions)
    }


val HeadingRotation = SphericalUtil.computeHeading(LatLng(startValue[0]-0.00002, startValue[1]+0.000035), LatLng(endValue[0]-0.00002, endValue[1]+0.000035))

    val marOptions = MarkerOptions()
    marOptions
        .position(LatLng(endValue[0]-0.00002, endValue[1]+0.000035))
        .icon(bitmapFromVector(ACTIVITY.applicationContext, R.drawable.ic_navigation_arrow))
        .anchor(0.5f, -0.2f)
        .rotation(HeadingRotation.toFloat())
        .flat(true)
        .draggable(false)


    ACTIVITY.mMap?.let {
        ACTIVITY.lineMarker = it.addMarker(marOptions)!!
    }

}

fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    // below line is use to generate a drawable.
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    // below line is use to set bounds to our vector drawable.
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    // below line is use to create a bitmap for our drawable which we have added.
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    // below line is use to add bitmap in our canvas.
    val canvas = Canvas(bitmap)


    // below line is use to draw our vector drawable in canvas.
    vectorDrawable.draw(canvas)



    // after generating our bitmap we are returning our bitmap.
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}