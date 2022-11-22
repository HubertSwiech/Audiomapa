package com.example.gg_dyplom

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.example.gg_dyplom.databinding.FragmentBottomPanelBinding

class FragmentBottomPanel(bottomButton2: Button, floor: TextView, db2: DatabaseGeodes, btn: Button) : Fragment(R.layout.fragment_bottom_panel) {

    lateinit var binding: FragmentBottomPanelBinding
    lateinit var ACTIVITY: MapsActivity
    val bottomButton = bottomButton2
//    private var ttsHelper: TtsHelper? = null
    var floorTextView = floor
    var comButton = btn
    var targetText = ""
    var db =db2

    lateinit var hideBtn: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ACTIVITY = context as MapsActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBottomPanelBinding.inflate(layoutInflater)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Handler().postDelayed({
            hideBtn.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }, 2000)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_bottom_panel, container, false)


        if(ACTIVITY.mapCircle!=null){ ACTIVITY.mapCircle.remove() }
        if(ACTIVITY.mapLine!=null){ ACTIVITY.mapLine.remove() }
        if(ACTIVITY.lineMarker!=null){ ACTIVITY.lineMarker.remove() }

        hideBtn = v.findViewById<Button>(R.id.hideFrag)
//        hideBtn.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        hideBtn.setOnClickListener{
            hideFragment()
            bottomButton.visibility = View.VISIBLE
            ACTIVITY.clickedPanel = false
//            if(ACTIVITY.mapCircle!=null){
//                ACTIVITY.mapCircle.remove()
//            }
//            if(ACTIVITY.mapLine!=null){
//                ACTIVITY.mapLine.remove()
//            }
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

        //Ukrywanie klawiatury kliknięciem w tło na panelu
        v.setOnTouchListener { v, event ->
            closeKeyboard()
            v?.onTouchEvent(event) ?: true
        }

//        v.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                closeKeyboard()
//                return v?.onTouchEvent(event) ?: true
//            }
//        })

        val navBtn = v.findViewById<Button>(R.id.nawigujRead)
        val nrBtn = v.findViewById<Button>(R.id.numerRead)
        val startEditText = v.findViewById<EditText>(R.id.startEdit)
        val komEditText = v.findViewById<EditText>(R.id.komunikat)
        val stopBtn = v.findViewById<Button>(R.id.stop)
        val dropList = v.findViewById<Spinner>(R.id.spnTest)

//        val db = Database(ACTIVITY.applicationContext)
//        ttsHelper = TtsHelper(ACTIVITY.applicationContext, ACTIVITY)

        // text to speech system
        startEditText.setText(ACTIVITY.pointNumber)

        //Ukrywanie klawiatury klinięciem w spinnera
        dropList.setOnTouchListener { v, event ->
            closeKeyboard()
            v?.onTouchEvent(event) ?: true
        }

        //Ukrywanie klawiatury klinięciem w pole tekstowe do odczytywania komunikatu
        komEditText.setOnTouchListener { v, event ->
            closeKeyboard()
            v?.onTouchEvent(event) ?: true
        }

        val targetList: MutableList<String> = arrayListOf("-")

        if(ACTIVITY.pointNumber != ""){//Jeżeli został wybrany marker
            setSpinner(db, targetList, dropList, navBtn, startEditText, komEditText, stopBtn)
        }

        startEditText.addTextChangedListener(object : TextWatcher {
//Dynamiczne usawianie numerów celu po zmianie
            override fun afterTextChanged(s: Editable) {
                targetList.clear()
                targetList.add("-")
                ACTIVITY.pointNumber = s.toString()
                if(ACTIVITY.pointNumber != ""){
                    setSpinner(db, targetList, dropList, navBtn, startEditText, komEditText, stopBtn)
               }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        ACTIVITY.ttsHelper?.ttsLocation(ACTIVITY.applicationContext, startEditText.text.toString(), nrBtn, db, ACTIVITY.fragmentManager, komEditText, stopBtn, ACTIVITY, floorTextView)

        return v
    }

    //powtórka
    private fun hideFragment() {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
        transaction.remove(this)
        ACTIVITY.ttsHelper?.stopSpeaking()
        transaction.commit()
    }

    fun setSpinner(db: DatabaseGeodes, targetList: MutableList<String>, dropList: Spinner, navBtn: Button, startEditText: EditText, komEditText: EditText, stopBtn: Button){
        db.open()
        targetList.clear()
        val targets = db.getTarget(ACTIVITY.pointNumber)
        for(i in targets){
            targetList.add(i)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(ACTIVITY, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, targetList)
        dropList.adapter = adapter
        if(targets.isNotEmpty()){
            ////ondropdown listener albo zmienna globalna w maps z numerem z dropdown
            navBtn.isEnabled = true
            navBtn.setBackgroundResource(R.drawable.bottom_panel_button)
            dropList.setBackgroundResource(R.drawable.spinner_background)
            ACTIVITY.targetNumber = dropList.selectedItem.toString()

            db.close()

            ACTIVITY.ttsHelper?.ttsNavigation(ACTIVITY.applicationContext, startEditText, dropList, navBtn, db, ACTIVITY.fragmentManager, komEditText, stopBtn, ACTIVITY, floorTextView)
        } else {
            navBtn.isEnabled = false
            navBtn.setBackgroundResource(R.drawable.bottom_panel_disabled)
            dropList.setBackgroundResource(R.drawable.spinner_background_empty)
        }
    }


    fun closeKeyboard(){
        val view: View? = ACTIVITY.currentFocus
        if(view != null){
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

