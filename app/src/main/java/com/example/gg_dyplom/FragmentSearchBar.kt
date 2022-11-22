package com.example.gg_dyplom

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.*
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.model.LatLng


class FragmentSearchBar(list: MutableMap<Int, MutableList<String>>, floor: TextView) : Fragment() {

var poiMap = list
    var floorNumber = floor
    lateinit var ACTIVITY: MapsActivity
    lateinit var searchBar: SearchView
    lateinit var back: Button



    override fun onAttach(context: Context) {
        super.onAttach(context)
        ACTIVITY = context as MapsActivity
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Handler().postDelayed({
            back.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }, 1000)
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.poiBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.locBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.ostBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.menu).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.searchButton).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.bottomPanelBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.commentListBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        ACTIVITY.window.decorView.rootView.findViewById<TextView>(R.id.floor).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
//        ACTIVITY.mapFragment.view?.findViewWithTag<Button>("GoogleMapCompass")?.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS


    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_search_bar, container, false)

        searchBar = v.findViewById<SearchView>(R.id.searchBar)
        val list = v.findViewById<ListView>(R.id.poiList)
        val roomsList = v.findViewById<ListView>(R.id.roomsList)

        back = v.findViewById<Button>(R.id.back)
        back.setOnClickListener{
//            searchBar.animate()
////            .scaleX(1f)
//                .setDuration(700)
//                .translationX(0f)
//                .start();
            this.view?.announceForAccessibility("Zamknięto widok wyszukiwania punktów.")

//            Handler().postDelayed({
                val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top)
                transaction.remove(this)
                transaction.commit()
//            }, 700)

            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.poiBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.locBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.ostBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.menu).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.searchButton).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.bottomPanelBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.commentListBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            ACTIVITY.window.decorView.rootView.findViewById<TextView>(R.id.floor).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.mapFragment.view?.findViewWithTag<Button>("GoogleMapCompass")?.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }

//        searchBar.animate()
////            .scaleX(1f)
//            .setDuration(700)
//            .translationX(-110f)
//            .start();


        var poiList = mutableListOf<String>()
        poiMap.forEach(){
            poiList.add(it.value[1])
        }

        var roomsMutableList = mutableListOf<String>()
        ACTIVITY.roomMap.forEach(){
            roomsMutableList.add(it.key)
        }





        val listAdapter: ArrayAdapter<String?> =
            object : ArrayAdapter<String?>(ACTIVITY, android.R.layout.simple_list_item_1, poiList.sorted()) {
                override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    return view
                }
            }

        val roomsAdapter: ArrayAdapter<String?> =
            object : ArrayAdapter<String?>(ACTIVITY, android.R.layout.simple_list_item_1, roomsMutableList.sorted()) {
                override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    return view
                }
            }

        // on below line setting list adapter to our list view.
        list.adapter = listAdapter
        roomsList.adapter = roomsAdapter

        searchingPOIList(poiList, list, listAdapter)
        toggleListViewButtons(v, list, poiList, listAdapter, roomsList, roomsMutableList, roomsAdapter)

        return v
    }

    private fun toggleListViewButtons(v: View, poiList: ListView, poiMutableList: MutableList<String>, listAdapter: ArrayAdapter<String?>, roomsList: ListView, roomsMutableList: MutableList<String>, roomsAdapter: ArrayAdapter<String?>){
        val poiButton = v.findViewById<Button>(R.id.poiListButton)
        val roomsButton = v.findViewById<Button>(R.id.roomListButton)

        poiButton.setOnClickListener{
            poiList.visibility = VISIBLE
            roomsList.visibility = INVISIBLE
            poiButton.background = resources.getDrawable(R.drawable.poi_list_button_click)
            roomsButton.background = resources.getDrawable(R.drawable.rooms_list_button)
            searchBar.background= resources.getDrawable(R.drawable.search_bar_edit)
            searchBar.queryHint = "ODSZUKAJ POI"
            searchingPOIList(poiMutableList, poiList, listAdapter)
        }
        roomsButton.setOnClickListener{
            poiList.visibility = INVISIBLE
            roomsList.visibility = VISIBLE
            poiButton.background = resources.getDrawable(R.drawable.poi_list_button)
            roomsButton.background = resources.getDrawable(R.drawable.rooms_list_button_click)
            searchBar.background= resources.getDrawable(R.drawable.search_bar_edit_rooms)
            searchBar.queryHint = "ODSZUKAJ NUMER SALI"
            searchingRoomsList(roomsMutableList, roomsList, roomsAdapter)
        }
    }

    private fun searchingPOIList(poiList: MutableList<String>, list: ListView, listAdapter: ArrayAdapter<String?>){
//        searchBar = v.findViewById<SearchView>(R.id.searchBar)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are checking
                // if query exist or not.
                if (poiList.contains(query)) {
                    // if query exist within list we
                    // are filtering our list adapter.
                    if (query != null) {
                        setSearchingPOIOnMap(query)
                    }
//                    listAdapter.filter.filter(query)
                } else {
                    // if query is not present we are displaying a toast message as no  data found..
//                    Toast.makeText(ACTIVITY, "No Language found..", Toast.LENGTH_LONG).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we are filtering our adapter with
                // new text on below line.

                listAdapter.filter.filter(newText)
                return false
            }
        })
//
//        searchBar.setOnKeyListener(object : View.OnKeyListener {
//            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
//                // If the event is a key-down event on the "enter" button
//                if (event.getAction() === KeyEvent.ACTION_DOWN &&
//                    keyCode == KeyEvent.KEYCODE_ENTER
//                ) {
//                    // Perform action on key press
//
//                    Toast.makeText(ACTIVITY, searchBar.query, Toast.LENGTH_SHORT)
//                        .show()
//                    return true
//                }
//                return false
//            }
//        })


        list.setOnItemClickListener{ parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            setSearchingPOIOnMap(selectedItem)
//            poiMap.filter { it.value[1] == selectedItem }.forEach{
//                drawCircle(it.value[0], ACTIVITY)
//                switchFloorOverlay(it.value[0], ACTIVITY, floorNumber)
//                ACTIVITY.pointNumber = it.value[0]
//            }
//            ACTIVITY.ttsHelper?.mTTS?.speak(selectedItem, TextToSpeech.QUEUE_FLUSH, null)
//            this.view?.announceForAccessibility("Zamknięto widok wyszukiwania punktów. Na mapie zaznaczono $selectedItem")
//            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
//            transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top)
//            transaction.hide(this)
//            transaction.commit()
//
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.poiBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.locBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.ostBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.menu).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.searchButton).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.bottomPanelBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.commentListBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<TextView>(R.id.floor).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }
    }

    private fun searchingRoomsList(roomsMutableList: MutableList<String>, roomsList: ListView, roomsAdapter: ArrayAdapter<String?>){
//        searchBar = v.findViewById<SearchView>(R.id.searchBar)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are checking
                // if query exist or not.
                if (roomsMutableList.contains(query)) {
                    // if query exist within list we
                    // are filtering our list adapter.
                    if (query != null) {
                        setSearchingRoomOnMap(query)
                    }
//                    roomsAdapter.filter.filter(query)
                } else {
                    // if query is not present we are displaying a toast message as no  data found..
//                    Toast.makeText(ACTIVITY, "No Language found..", Toast.LENGTH_LONG).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we are filtering our adapter with
                // new text on below line.

                roomsAdapter.filter.filter(newText)
                return false
            }
        })

        roomsList.setOnItemClickListener{ parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String

            setSearchingRoomOnMap(selectedItem)
//            ACTIVITY.roomMap.filter { it.key == selectedItem }.forEach{
//                drawCircle("0", ACTIVITY, LatLng(it.value[0]-0.00002,it.value[1]+0.000035))
//                switchFloorOverlay(it.value[0].toString(), ACTIVITY, floorNumber, it.value[2].toInt().toString())
//            }
//            ACTIVITY.ttsHelper?.mTTS?.speak("Sala o numerze $selectedItem.", TextToSpeech.QUEUE_FLUSH, null)
//            this.view?.announceForAccessibility("Zamknięto widok wyszukiwania punktów. Na mapie zaznaczono salę o numerze $selectedItem")
//            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
//            transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top)
//            transaction.hide(this)
//            transaction.commit()
//
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.poiBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.locBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.ostBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.menu).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.searchButton).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.bottomPanelBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.commentListBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<TextView>(R.id.floor).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }
    }

    private fun setSearchingRoomOnMap(query: String){
        ACTIVITY.roomMap.filter { it.key == query }.forEach{
            drawCircle("0", ACTIVITY, LatLng(it.value[0]-0.00002,it.value[1]+0.000035))
            switchFloorOverlay(it.value[0].toString(), ACTIVITY, floorNumber, it.value[2].toInt().toString())
        }
        ACTIVITY.ttsHelper?.mTTS?.speak("Sala o numerze $query.", TextToSpeech.QUEUE_FLUSH, null)
//                    this.view?.announceForAccessibility("Zamknięto widok wyszukiwania punktów. Na mapie zaznaczono salę o numerze $query")
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top)
        fragmentManager?.findFragmentById(R.id.fragmentContainer)
            ?.let { transaction.remove(it) }

        transaction.commit()

        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.poiBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.locBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.ostBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.menu).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.searchButton).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.bottomPanelBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.commentListBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<TextView>(R.id.floor).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    private fun setSearchingPOIOnMap(selectedItem: String){
        poiMap.filter { it.value[1] == selectedItem }.forEach{
            drawCircle(it.value[0], ACTIVITY)
            switchFloorOverlay(it.value[0], ACTIVITY, floorNumber)
            ACTIVITY.pointNumber = it.value[0]
        }
        ACTIVITY.ttsHelper?.mTTS?.speak(selectedItem, TextToSpeech.QUEUE_FLUSH, null)
        this.view?.announceForAccessibility("Zamknięto widok wyszukiwania punktów. Na mapie zaznaczono $selectedItem")
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top)
        transaction.hide(this)
        transaction.commit()

        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.poiBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.locBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.ostBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.menu).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.searchButton).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.bottomPanelBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.commentListBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        ACTIVITY.window.decorView.rootView.findViewById<TextView>(R.id.floor).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

}


