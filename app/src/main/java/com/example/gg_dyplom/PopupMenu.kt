package com.example.gg_dyplom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction

class PopupMenu(number: String, db: DatabaseGeodes, dbCom: DatabaseCom) : DialogFragment() {

    val pointNumber2 = number
    val dbkomunikat = db
    val dbComment = dbCom
    lateinit var ACTIVITY: MapsActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ACTIVITY = context as MapsActivity
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return MaterialAlertDialogBuilder(requireActivity(), R.style.PopupStyle)
//            .setPositiveButton("OK", null)
//            .create()
//    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedINstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.popup_menu, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val idstart = v.findViewById<EditText>(R.id.popupEditStart)
        val text = v.findViewById<EditText>(R.id.popupText)
        val zapiszbtn = v.findViewById<Button>(R.id.popupZapisz)
        val dropList = v.findViewById<Spinner>(R.id.spnTestPop)
        val back = v.findViewById<Button>(R.id.backPopup)

        idstart.setText(ACTIVITY.pointNumber)
        val targetList: MutableList<String> = arrayListOf("-")

        v.setOnTouchListener { v, event ->
            closeKeyboard()
            v?.onTouchEvent(event) ?: true
        }

        back.setOnClickListener{
            this.dismiss()
        }


        if(ACTIVITY.pointNumber != ""){//Jeżeli został wybrany marker
            setSpinner(dbkomunikat, targetList, dropList)
        } else {
            setSpinner(dbkomunikat, targetList, dropList)
        }

        idstart.addTextChangedListener(object : TextWatcher {
            //Dynamiczne usawianie numerów celu po zmianie
            override fun afterTextChanged(s: Editable) {
                targetList.clear()
                targetList.add("-")
                ACTIVITY.pointNumber = s.toString()
                if(ACTIVITY.pointNumber != ""){
                    setSpinner(dbkomunikat, targetList, dropList)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })





        zapiszbtn.setOnClickListener{

            dbComment.open()
            dbComment.insertData(idstart.text.toString(),
                dropList.selectedItem.toString(),
                text.text.toString())
            dbComment.close()
            Toast.makeText(context, "Dodano komentarz", Toast.LENGTH_SHORT).show()
            closeKeyboard()

            val currentFragmentComment = ACTIVITY.supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if(currentFragmentComment is FragmentComments) {
                var frag = ACTIVITY.fragmentManager.findFragmentById(R.id.ButtonAction)
                if (frag != null) {
                    val transaction: FragmentTransaction = ACTIVITY.fragmentManager.beginTransaction()
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    transaction.remove(frag)
                    transaction.commit()
                }

                var frag2 = ACTIVITY.fragmentManager.findFragmentById(R.id.fragmentContainer)
                if (frag2 != null) {
                    val transaction: FragmentTransaction = ACTIVITY.fragmentManager.beginTransaction()
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    transaction.remove(frag2)
                    transaction.commit()
                }

                val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                transaction.replace(R.id.fragmentContainer, FragmentComments(ACTIVITY.floorNumber, ACTIVITY.fragmentManager))
                transaction.commitAllowingStateLoss()
//                hideFragment(R.id.fragmentContainer, R.anim.enter_from_right, R.anim.exit_to_left)
            }
            this.dismiss()
        }


        return v
        }



    fun setSpinner(db: DatabaseGeodes, targetList: MutableList<String>, dropList: Spinner){
        db.open()
        targetList.clear()
        val targets = db.getTarget(ACTIVITY.pointNumber)
        targetList.add("-")
        for(i in targets){
            targetList.add(i)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(ACTIVITY, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, targetList)
        dropList.adapter = adapter
        if(targets.isNotEmpty()){
            ////ondropdown listener albo zmienna globalna w maps z numerem z dropdown
            dropList.setBackgroundResource(R.drawable.spinner_background)
            ACTIVITY.targetNumber = dropList.selectedItem.toString()
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

