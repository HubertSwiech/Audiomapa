package com.example.gg_dyplom

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentTransaction
import com.example.gg_dyplom.databinding.FragmentBottomPanelBinding
import com.example.gg_dyplom.databinding.FragmentQROptionsBinding


class FragmentQROptions(btn: Button, locationCom: String, zoneCom: String, floorCom: String) : Fragment() {

    lateinit var binding: FragmentQROptionsBinding
    lateinit var ACTIVITY: MapsActivity

    var comButton = btn
    lateinit var hideBtn: Button
    val locationText = locationCom
    val zoneText = zoneCom
    val floorText = floorCom


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ACTIVITY = context as MapsActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentQROptionsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val v = inflater.inflate(R.layout.fragment_q_r_options, container, false)

        hideBtn = v.findViewById<Button>(R.id.hideFrag)
//        hideBtn.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        hideBtn.setOnClickListener{
            hideFragment()
            ACTIVITY.bottomButton.visibility = View.VISIBLE
            ACTIVITY.clickedPanel = false

            closeKeyboard()
            this.view?.announceForAccessibility("Zamknięto panel do odczytywania komunikatów.")

            val animation: Animation = TranslateAnimation(0F, 0F, -750F, 0F)
            animation.duration = 500
            animation.fillAfter = true
            comButton.startAnimation(animation)
            comButton.setY(comButton.y + 750F)
            ACTIVITY.scannerBtn.startAnimation(animation)
            ACTIVITY.scannerBtn.setY(ACTIVITY.scannerBtn.y + 750F)

        }


        val locationBtn = v.findViewById<Button>(R.id.readLocation)
        val zoneBtn = v.findViewById<Button>(R.id.readZones)
        val floorBtn = v.findViewById<Button>(R.id.readFloor)
        val komEditText = v.findViewById<EditText>(R.id.komunikat)
        val stopBtn = v.findViewById<Button>(R.id.stop)

        ACTIVITY.ttsHelper?.ttsLocation(ACTIVITY.applicationContext, locationText, locationBtn, ACTIVITY.db, ACTIVITY.fragmentManager, komEditText, stopBtn, ACTIVITY, ACTIVITY.floorNumber, "nieRysuj")
        ACTIVITY.ttsHelper?.ttsLocation(ACTIVITY.applicationContext, zoneText, zoneBtn, ACTIVITY.db, ACTIVITY.fragmentManager, komEditText, stopBtn, ACTIVITY, ACTIVITY.floorNumber, "nieRysuj")
        ACTIVITY.ttsHelper?.ttsLocation(ACTIVITY.applicationContext, floorText, floorBtn, ACTIVITY.db, ACTIVITY.fragmentManager, komEditText, stopBtn, ACTIVITY, ACTIVITY.floorNumber, "nieRysuj")

        return v
    }

    private fun hideFragment() {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
        transaction.remove(this)
        ACTIVITY.ttsHelper?.stopSpeaking()
        transaction.commit()
    }

    fun closeKeyboard(){
        val view: View? = ACTIVITY.currentFocus
        if(view != null){
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}