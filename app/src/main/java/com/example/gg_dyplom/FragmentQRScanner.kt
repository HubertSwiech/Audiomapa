package com.example.gg_dyplom

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import me.dm7.barcodescanner.zxing.ZXingScannerView
import com.google.zxing.Result
import java.lang.Error

class FragmentQRScanner(floor: TextView): DialogFragment(), PermissionListener, ZXingScannerView.ResultHandler {

    lateinit var ACTIVITY: MapsActivity
    lateinit var scannerView: ZXingScannerView
    lateinit var txtResult: TextView
    var qrCodeContentMap = mutableMapOf<String, String>()
    var floorNumber = floor
    private val TAG = "pw.qrCode"

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


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_qr_scanner, container, false)

        scannerView = v.findViewById(R.id.scanner)
        val backButton = v.findViewById<Button>(R.id.backBtn)


        Dexter.withActivity(ACTIVITY)
            .withPermission(android.Manifest.permission.CAMERA)
            .withListener(this)
            .check()


        backButton.setOnClickListener{
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.setCustomAnimations( R.anim.enter_from_left,
                R.anim.exit_to_left)
            transaction.remove(this)
//        ACTIVITY.ttsHelper?.stopSpeaking()
            transaction.commit()
            scannerView.stopCamera()
        }


        return v
    }

    override fun handleResult(rawResult: Result) {
        val text = rawResult.getText().toString()
        try {
            if(text.startsWith("audiomapa\n")){
                val arr = text.split("\n")

                arr.forEach {
                    if(it != "audiomapa"){
                        val keyValue = it.split(":")
                        qrCodeContentMap[keyValue[0]] = keyValue[1]
                    }
                }

                ACTIVITY.db.open()
                val pointsToRead = qrCodeContentMap["points"]?.split(",")
                var message = ""
                pointsToRead?.forEach {
                    message += ACTIVITY.db.getLocation(it)
                }

                ACTIVITY.db.close()
                ACTIVITY.pointNumber = pointsToRead?.get(0) ?: ""
                ACTIVITY.ttsHelper?.mTTS?.speak(message, TextToSpeech.QUEUE_FLUSH, null)

                val localizationCoordinates = qrCodeContentMap["coordinates"]?.split(",")
                val latLngLocalizationCoordinates = LatLng((localizationCoordinates?.get(1)?.toDouble()?: 0.0)-0.00002,(localizationCoordinates?.get(0)?.toDouble()?: 0.0)+0.000035)
                drawCircle(pointsToRead?.get(0) ?: "", ACTIVITY, latLngLocalizationCoordinates)

                val localizationFloorNumber = qrCodeContentMap["floor"]
                switchFloorOverlay(pointsToRead?.get(0) ?: "", ACTIVITY, floorNumber, localizationFloorNumber?: "null")

                val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                transaction.setCustomAnimations( R.anim.enter_from_left, R.anim.exit_to_left)
                transaction.remove(this)
                transaction.commit()
            } else {
                Toast.makeText(context, "Ten kod QR nie jest kompatybilny z aplikacją.", Toast.LENGTH_SHORT).show()

                val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                transaction.setCustomAnimations( R.anim.enter_from_left, R.anim.exit_to_left)
                transaction.remove(this)
                transaction.commit()
            }

        } catch (e:Error){
            Log.e(TAG, "Błąd przy odczytywaniu danych z kodu qr.")
        }

        scannerView.stopCamera()
    }


    override fun onPermissionGranted(response: PermissionGrantedResponse) {
        // This method will be called when the permission is denied
        scannerView.setResultHandler (this)
        scannerView.startCamera()
    }

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken?) {
        // This method will be called when the user rejects a permission request
        // You must display a dialog box that explains to the user why the application needs this permission
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse) {
        // This method will be called when the permission is granted

    }

    override fun onPause() {
        scannerView.stopCamera()
        super.onPause()
    }
}

