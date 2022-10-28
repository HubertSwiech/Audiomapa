//package com.example.gg_dyplom
//
////import DBHelper
//import com.example.gg_dyplom.Database
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.View
//import android.widget.*
//import android.widget.PopupMenu
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentTransaction
//import com.example.gg_dyplom.databinding.ActivityMainBinding
//import com.github.chrisbanes.photoview.PhotoView
//import com.google.firebase.analytics.FirebaseAnalytics
//import com.google.firebase.database.DatabaseReference
//import pl.droidsonroids.gif.GifImageView
//import java.io.*
//
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var analytics: FirebaseAnalytics
//    private lateinit var database: DatabaseReference
//    lateinit var binding: ActivityMainBinding
//
//    @SuppressLint(
//        "UseSwitchCompatOrMaterialCode", "ClickableViewAccessibility",
//        "UseCompatLoadingForDrawables", "ResourceType"
//    )
//    private var ttsHelper: TtsHelper? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        ttsHelper = TtsHelper(this)
//
//        val fragmentManager = supportFragmentManager
//        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
//        transaction.replace(R.id.fragmentContainer, Fragment1())
//        transaction.commit()
//
//
//        binding.menu.setOnClickListener{
//            replaceFragment(FragmentMenu())
//        }
//
//        binding.strefy.setOnClickListener{
//            val fragmentManager = supportFragmentManager
//            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
//            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
//            transaction.replace(R.id.fragmentContainer, FragmentStrefy())
//            transaction.commit()
//        }
//
//
//        try {
//            this.supportActionBar!!.hide()
//        } catch (e: NullPointerException) {
//        }
//
//        val navBtn = findViewById<Button>(R.id.nawigujRead)
//        val nrBtn = findViewById<Button>(R.id.numerRead)
//        val nrEditText = findViewById<EditText>(R.id.numerEdit)
//        val startEditText = findViewById<EditText>(R.id.startEdit)
//        val endEditText = findViewById<EditText>(R.id.endEdit)
//        val dot = findViewById<GifImageView>(R.id.dot0)
//        val komEditText = findViewById<EditText>(R.id.komunikat)
////        val wpiszPopup = findViewById<Button>(R.id.wpisz)
//        copyDatabase()
//
//        val db = com.example.gg_dyplom.Database(this)
//
//        ttsHelper?.ttsLocation(this, nrEditText, nrBtn, db, fragmentManager, dot, komEditText)
//        ttsHelper?.ttsNavigation(this, startEditText, endEditText, navBtn, db, fragmentManager, dot, komEditText)
//
//        dot.setOnClickListener(){
//            dot.visibility = View.INVISIBLE
//        }
//
////        wpiszPopup.setOnClickListener{
////            val popup = PopupWindow()
////            val view = layoutInflater.inflate(R.layout.popup_menu, null)
////            window.setContentView(view)
//////            popup.inflate(R.layout.popup_menu)
////            popup.showAsDropDown(wpiszPopup)
////        }
////
////        wpiszPopup.setOnClickListener{
////            val dialog = PopupMenu()
//////            val bundle = Bundle()
//////            bundle.put
//////            getIntent().putExtra("database", db)
////
////            dialog.show(supportFragmentManager, "customDialog")
////        }
//
//
//    }
//
//
//
//    private fun replaceFragment(fragment : Fragment) {
//        val fragmentManager = supportFragmentManager
//        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left)
//        transaction.replace(R.id.fragmentContainer2, fragment)
//        transaction.commit()
//    }
//
//    private fun copyDatabase(){
//        //get context by calling "this" in activity or getActivity() in fragment
////call this if API level is lower than 17  String appDataPath = "/data/data/" + context.getPackageName() + "/databases/"
//        //get context by calling "this" in activity or getActivity() in fragment
////call this if API level is lower than 17  String appDataPath = "/data/data/" + context.getPackageName() + "/databases/"
//        val appDataPath: String = this.getApplicationInfo().dataDir
//
//        val dbFolder = File("$appDataPath/databases") //Make sure the /databases folder exists
//
//        dbFolder.mkdir() //This can be called multiple times.
//
//
//        val dbFilePath = File("$appDataPath/databases/gmach_glowny_nowy.db")
//
//        try {
//            val inputStream: InputStream = this.getAssets().open("gmach_glowny_nowy.db")
//            val outputStream: OutputStream = FileOutputStream(dbFilePath)
//            val buffer = ByteArray(1024)
//            var length: Int
//            while (inputStream.read(buffer).also { length = it } > 0) {
//                outputStream.write(buffer, 0, length)
//            }
//            outputStream.flush()
//            outputStream.close()
//            inputStream.close()
//        } catch (e: IOException) {
//            //handle
//        }
//    }
//
//
//
//    override fun onPause() {
//        ttsHelper?.stopSpeaking()
//        super.onPause()
//    }
//}
//
//
//
//
//
