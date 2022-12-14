package com.example.gg_dyplom

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction

class PopupMenuEdit(
    db: Database,
    dbCom: DatabaseCom,
    idx: MutableMap.MutableEntry<Int, List<String>>,
    comments2: FragmentComments
) : DialogFragment() {


    val dbkomunikat = db
    val dbComment = dbCom
    val data = idx
    val comments = comments2
    lateinit var ACTIVITY: MapsActivity
    lateinit var back: Button

    override fun onAttach( context: Context) {
        super.onAttach(context)
        ACTIVITY = context as MapsActivity
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Handler().postDelayed({
            back.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }, 2000)

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedINstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.fragment_popup_menu_edit, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val idstart = v.findViewById<EditText>(R.id.popupEditStart)
        val text = v.findViewById<EditText>(R.id.popupText)
        val zapiszbtn = v.findViewById<Button>(R.id.popupZapisz)
        val dropList = v.findViewById<Spinner>(R.id.spnTestPop)
        back = v.findViewById<Button>(R.id.backPopupEdit)

        back.setOnClickListener{
            this.dismiss()
            this.view?.announceForAccessibility("Zamkni??to okno do edycji komentarzy. Wy??wietlono widok komentarzy")
        }

        idstart.setText(data.value[0])
        ACTIVITY.pointNumber = data.value[0]

        text.setText(data.value[2])

        val targetList: MutableList<String> = arrayListOf("-")
        dbkomunikat.open()
        targetList.clear()
        val targets = dbkomunikat.getTarget(ACTIVITY.pointNumber)
//        targetList.add(data.value[1])
        targetList.add("-")
        for(i in targets){
            targetList.add(i)
        }
        dbkomunikat.close()
//        val adapter: ArrayAdapter<String> = ArrayAdapter(ACTIVITY, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, targetList)
//        dropList.adapter = adapter
//        dropList.setSelection(adapter.getPosition(data.value[1]), false)
//        println("wwwwwwww: "+ adapter.getPosition(data.value[1]))


        v.setOnTouchListener { view, event ->
            closeKeyboard()
//            dropList.setSelection(adapter.getPosition(data.value[1]), false)
            view?.onTouchEvent(event) ?: true
        }


        if(idstart.text.toString() != ""){//Je??eli zosta?? wybrany marker
            setSpinner(dbkomunikat, targetList, dropList, idstart)
        }

        idstart.addTextChangedListener(object : TextWatcher {
            //Dynamiczne usawianie numer??w celu po zmianie
            override fun afterTextChanged(s: Editable) {
                targetList.clear()
                targetList.add("-")
                ACTIVITY.pointNumber = s.toString()
                if(ACTIVITY.pointNumber != ""){
                    setSpinner(dbkomunikat, targetList, dropList, idstart)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })



        zapiszbtn.setOnClickListener{

                dbComment.open()
                dbComment.updateRow(data.key.toString(), ACTIVITY.pointNumber, dropList.selectedItem.toString(), text.text.toString())
                dbComment.close()
                Toast.makeText(context, "Edytowano komentarz", Toast.LENGTH_SHORT).show()

            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.fragmentContainer, comments)
            transaction.commitAllowingStateLoss()
            this.dismiss()
            this.view?.announceForAccessibility("Zamkni??to okno do edycji komentarzy. Wy??wietlono widok komentarzy")
            }


        return v
    }



    fun setSpinner(db: Database, targetList: MutableList<String>, dropList: Spinner, idstart: EditText){
        db.open()
        targetList.clear()
        val targets = db.getTarget(ACTIVITY.pointNumber)
        targetList.add("-")
        for(i in targets){
            targetList.add(i)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(ACTIVITY, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, targetList)
        dropList.adapter = adapter
        dropList.setSelection(adapter.getPosition(data.value[1]))
        if(targets.isNotEmpty()){
            ////ondropdown listener albo zmienna globalna w maps z numerem z dropdown
            dropList.setBackgroundResource(R.drawable.spinner_background)
//            ACTIVITY.targetNumber = dropList.selectedItem.toString()
//            idend.setText(dropList.selectedItem.toString())
            db.close()

        } else {

            dropList.setBackgroundResource(R.drawable.spinner_background_empty)
        }
    }

    fun closeKeyboard(){
        val view: View? = ACTIVITY.currentFocus
        if(view != null){
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        view?.clearFocus();
    }


}

