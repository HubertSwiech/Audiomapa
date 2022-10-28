//package com.example.gg_dyplom
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.View
//import android.widget.Button
//import android.widget.Switch
//import com.github.chrisbanes.photoview.PhotoView
//
//class MainActivity2 : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main2)
//        try {
//            this.supportActionBar!!.hide()
//        } catch (e: NullPointerException) {
//        }
//
//        val wybor  = findViewById<Switch>(R.id.switch1)
//        val pomieszczenia  = findViewById<PhotoView>(R.id.rotate)
//        val strefy  = findViewById<PhotoView>(R.id.strefy)
//
//
//        wybor.setOnCheckedChangeListener{_, isChecked ->
//            if(isChecked) {
//                strefy.visibility = View.VISIBLE
//                pomieszczenia.visibility = View.INVISIBLE
//            }
//            else {
//                pomieszczenia.visibility = View.VISIBLE
//                strefy.visibility = View.INVISIBLE
//            }
//        }
//
//
//        val cofnijButton = findViewById<Button>(R.id.cofnij)
//        cofnijButton.setOnClickListener{
//            val intent = Intent(this, MapsActivity::class.java)
//            startActivity(intent)
//        }
//    }
//
//}