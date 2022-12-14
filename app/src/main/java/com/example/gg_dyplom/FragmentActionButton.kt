package com.example.gg_dyplom

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class FragmentActionButton(
    relativeLayout: RelativeLayout,
    fragmentComments: FragmentComments,
    flrTextView: TextView,
    i: MutableMap.MutableEntry<Int, List<String>>,
    supFM: FragmentManager,
    btn: Button
) : DialogFragment() {


    val rel = relativeLayout
    var comments = fragmentComments
    var floorTextView = flrTextView
    var data = i
    var supportFragmentManager = supFM
    var actionButton = btn
    lateinit var ACTIVITY: MapsActivity
    lateinit var czytaj: Button

//    val ttsHelper = ACTIVITY.ttsHelper
//    private var ttsHelper: TtsHelper? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        ACTIVITY = context as MapsActivity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onPause() {
        this.view?.announceForAccessibility("Zamknięto pasek z akcjami komentarza.")
//            Handler().postDelayed({
        actionButton.background = resources.getDrawable(R.drawable.action_button)
//                rel.setPadding(0,0,0,0)
//            }, 300)
        super.onPause()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Handler().postDelayed({
            czytaj.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }, 2000)
        actionButton.background = resources.getDrawable(R.drawable.action_button_clicked)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_action_button, container, false)

//        v.setOnTouchListener { v, event ->
//            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
//            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
//            transaction.hide(this)
//            transaction.commit()
//            this.view?.announceForAccessibility("Zamknięto pasek z akcjami komentarza.")
////            Handler().postDelayed({
//                actionButton.background = resources.getDrawable(R.drawable.action_button)
////                rel.setPadding(0,0,0,0)
////            }, 300)
//
//            v?.onTouchEvent(event) ?: true
//        }


        val usun = v.findViewById<Button>(R.id.usun)
        usun.setOnClickListener{
            ACTIVITY.dbCom.deleteRow(data.key)
            Handler().postDelayed({
                actionButton.background = resources.getDrawable(R.drawable.action_button)
//                rel.setPadding(0,0,0,0)
            }, 300)
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.hide(this)
//            transaction.detach(comments).attach(comments)
            this.view?.announceForAccessibility("Usunięto komentarz.")
            transaction.replace(R.id.fragmentContainer, FragmentComments(floorTextView, supportFragmentManager))
            transaction.commitAllowingStateLoss()
        }

        val lokalizuj = v.findViewById<Button>(R.id.lokalizuj)
        lokalizuj.setOnClickListener{
            drawCircle(data.value[0], ACTIVITY)
            if(isNumber(data.value[1])){
                drawLine(data.value[0],data.value[1], ACTIVITY)
            }
            switchFloorOverlay(data.value[0], ACTIVITY, floorTextView)
            ACTIVITY.pointNumber = data.value[0]
            ACTIVITY.targetNumber = data.value[1]
            this.view?.announceForAccessibility("Zamknięto widok komentarzy. Zaznaczono na mapie punkt o numerze ${data.value[0]} .")
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
            transaction.hide(this)
            transaction.hide(comments)
            transaction.commit()
        }

        val edytuj = v.findViewById<Button>(R.id.edytuj)
        edytuj.setOnClickListener{
            val dialog = PopupMenuEdit(ACTIVITY.db, ACTIVITY.dbCom, data, FragmentComments(floorTextView, supportFragmentManager))
            dialog.show(supportFragmentManager, "customDialog")
            Handler().postDelayed({
                actionButton.background = resources.getDrawable(R.drawable.action_button)
//                rel.setPadding(0,0,0,0)
            }, 300)
            this.view?.announceForAccessibility("Otworzono okienko do edycji komentarza.")
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.hide(this)
            transaction.commit()
        }

        czytaj = v.findViewById<Button>(R.id.czytaj)
        val przerwij = v.findViewById<Button>(R.id.przerwij)
//        ttsHelper = TtsHelper(ACTIVITY.applicationContext, ACTIVITY)
        ACTIVITY.ttsHelper?.ttsComment(ACTIVITY.applicationContext, data.key, czytaj,ACTIVITY.dbCom, przerwij)


        return v
    }





}

//private fun drawCircle(number: String, ACTIVITY: MapsActivity) {
//    if(ACTIVITY.mapCircle!=null){
//        ACTIVITY.mapCircle.remove()
//    }
//
//    for(i in ACTIVITY.markerMap){
//        if(i.key == number.toInt()){
//            // Instantiating CircleOptions to draw a circle around the marker
//            val circleOptions = CircleOptions()
//            // Specifying the center of the circle
//            circleOptions.center(LatLng(i.value[0]-0.00002, i.value[1]+0.000035))
//            // Radius of the circle
//            circleOptions.radius(5.0)
//            // Border color of the circle
//            circleOptions.strokeColor(0x70E50000)
//            // Fill color of the circle
//            circleOptions.fillColor(0x50ff0000)
//            // Border width of the circle
//            circleOptions.strokeWidth(50f)
//            circleOptions.zIndex(2F)
//            // Adding the circle to the GoogleMap
//            ACTIVITY.mapCircle = ACTIVITY.mMap.addCircle(circleOptions)
//            val cp = CameraPosition.Builder()
//                .bearing(-65f)
//                .target(LatLng(i.value[0]-0.00002, i.value[1]+0.000035))
//                .zoom(20f)
//                .build()
//            val cu = CameraUpdateFactory.newCameraPosition(cp)
//            ACTIVITY.mMap.animateCamera(cu)
//        }
//    }
//}
//
