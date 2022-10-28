package com.example.gg_dyplom
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {

    var mContext = context
    @SuppressLint("InflateParams")
    var mWindow: View = (context as Activity).layoutInflater.inflate(R.layout.custom_info_contents, null)

    private fun rendowWindowText(marker: Marker, view: View){
        val tvTitle = view.findViewById<TextView>(R.id.title)
        tvTitle.text = marker.title
    }

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

}