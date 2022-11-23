package com.example.gg_dyplom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class FragmentComments(
    flrTextView: TextView,
    supportFragmentManager: FragmentManager
) : Fragment() {

    var floorTextView = flrTextView
    var supFM = supportFragmentManager
    lateinit var backButton: Button

    lateinit var ACTIVITY: MapsActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ACTIVITY = context as MapsActivity
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Handler().postDelayed({
            backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }, 2000)
//        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.poiBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
//        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.locBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
//        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.ostBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
//        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.menu).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
//        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.searchButton).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
//        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.bottomPanelBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
//        ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.commentListBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
//        ACTIVITY.window.decorView.rootView.findViewById<TextView>(R.id.floor).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_comments, container, false)

        backButton = v.findViewById<Button>(R.id.back)
        backButton.setOnClickListener {
            var frag = requireFragmentManager().findFragmentById(R.id.ButtonAction)
            if (frag != null) {
                val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                transaction.remove(frag)
                transaction.commit()
            }
            this.view?.announceForAccessibility("Zamknięto widok z komentarzami.")
            val myFrag = fragmentManager?.findFragmentByTag("action_bar")
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
            transaction.remove(this)
            transaction.commit()
            ACTIVITY.ttsHelper?.stopSpeaking()

//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.poiBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.locBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.ostBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.menu).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.searchButton).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.bottomPanelBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<Button>(R.id.commentListBtn).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
//            ACTIVITY.window.decorView.rootView.findViewById<TextView>(R.id.floor).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES

        }

        val addCommentBtn = v.findViewById<Button>(R.id.addComment)

        addCommentBtn.setOnClickListener {
            ACTIVITY.pointNumber = ""
            val dialog = PopupMenu("null", ACTIVITY.db)
            dialog.show(ACTIVITY.fragmentManager, "customDialog")
        }

        fetchComments { comments ->
            activity?.runOnUiThread {
                v.setUp(comments)
            }
        }

        return v
    }

    @SuppressLint("RtlHardcoded")
    fun addTextViewInRow(i: String, tbrow: TableRow, width: Int, height: Int, gravity: Int) {
        val tv4 = TextView(ACTIVITY)

        tv4.setText(i)
        tv4.setLayoutParams(TableRow.LayoutParams(width, height))
        tv4.gravity = gravity
        tv4.setPadding(0, 15, 0, 15);
        tv4.textSize = 20F
        tv4.setTextColor(Color.BLACK)
        tbrow.addView(tv4)
    }

    private fun fetchComments(successCallback: (MutableMap<Int, List<String>>) -> Unit) {
        val answer: MutableMap<Int, List<String>> = mutableMapOf()
        val thread = Thread {
            ACTIVITY.dbComments.commentsDao().getAll().forEach()
            {
                answer[it.id] = listOf(it.lokalizacja, it.cel, it.komentarz) as List<String>
            }
            println("mmmmmmmmmm $answer")
            successCallback(answer)
        }
        thread.start()
    }

    private fun View.setUp(comments: MutableMap<Int, List<String>>) {
        val rel = findViewById<RelativeLayout>(R.id.rel)

        println("ppppp: $comments")
        val commentList = comments
//        MutableMap<Int, List<String>> = mutableMapOf()
//        ACTIVITY.dbCom.close()

        var stk = findViewById<TableLayout>(R.id.rows)

        var nr = 1

        for (i in commentList) {

            val tbrow = TableRow(ACTIVITY)
            tbrow.isClickable = true


            rel.setPadding(0, 0, 0, 130)
//            val tableRowParams = TableLayout.LayoutParams(
//                TableLayout.LayoutParams.FILL_PARENT,
//                TableLayout.LayoutParams.WRAP_CONTENT
//            )
//
//            tableRowParams.setMargins(30, 0, 30, 0)
//
//            tbrow.layoutParams = tableRowParams


            if (nr % 2 == 0) {
                tbrow.background = resources.getDrawable(R.drawable.comment_list_even)
//                tbrow.setBackground(0x00C9D8BC)
            } else {
                tbrow.background = resources.getDrawable(R.drawable.comment_list_odd)
//                tbrow.setBackgroundColor(0x00E2E2E2)
            }

            addTextViewInRow(
                nr.toString(),
                tbrow,
                160,
                TableRow.LayoutParams.MATCH_PARENT,
                Gravity.CENTER or Gravity.CENTER_VERTICAL
            )
            addTextViewInRow(
                i.value[0],
                tbrow,
                150,
                TableRow.LayoutParams.MATCH_PARENT,
                Gravity.CENTER or Gravity.CENTER_VERTICAL
            )
            addTextViewInRow(
                i.value[1],
                tbrow,
                130,
                TableRow.LayoutParams.MATCH_PARENT,
                Gravity.CENTER or Gravity.CENTER_VERTICAL
            )
            addTextViewInRow(
                i.value[2],
                tbrow,
                510,
                TableRow.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT
            )

            val btn = Button(ACTIVITY)


            tbrow.setOnClickListener {
                btn.background = resources.getDrawable(R.drawable.action_button_clicked)
                val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                transaction.replace(
                    R.id.ButtonAction,
                    FragmentActionButton(rel, this@FragmentComments, floorTextView, i, supFM, btn)
                )
                transaction.commit()
                announceForAccessibility("Wyświetlono pasek z akcjami do komentarza.")
            }

            btn.text = "Przycisk akcji dla komentarza."
            btn.textSize = 0F
            btn.background = resources.getDrawable(R.drawable.action_button)
            btn.setLayoutParams(TableRow.LayoutParams(50, TableRow.LayoutParams.WRAP_CONTENT))
            btn.setOnClickListener {
                btn.background = resources.getDrawable(R.drawable.action_button_clicked)
                val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                transaction.replace(
                    R.id.ButtonAction,
                    FragmentActionButton(rel, this@FragmentComments, floorTextView, i, supFM, btn)
                )
                transaction.commit()
                announceForAccessibility("Wyświetlono pasek z akcjami do komentarza.")
            }

            tbrow.addView(btn)

            stk.addView(tbrow)
            nr += 1
        }
    }
}





















