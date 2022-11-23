package com.example.gg_dyplom
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast


class DatabaseOpenHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, 1) {
    private var dataBase: SQLiteDatabase? = null
    private val context: Context
    @Throws(SQLException::class)

    fun openDataBase(): Boolean {
        val mPath = APP_DATA_PATH + DB_SUB_PATH
        //Note that this method assumes that the db file is already copied in place
        dataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE)
        return dataBase != null
    }

    @Synchronized
    override fun close() {
        if (dataBase != null) {
            dataBase!!.close()
        }
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {}
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        const val DB_NAME = "gmach_glowny_nowy.db"
        const val DB_SUB_PATH = "/databases/$DB_NAME"
        private var APP_DATA_PATH = ""
    }

    init {
        APP_DATA_PATH = context.applicationInfo.dataDir
        this.context = context
    }
}


class DatabaseGeodes(private val context: Context) {
    private var database: SQLiteDatabase? = null
    private val dbHelper: DatabaseOpenHelper = DatabaseOpenHelper(context)

    @Throws(SQLException::class)

    fun open(): DatabaseGeodes {
        dbHelper.openDataBase()
        dbHelper.close()
        database = dbHelper.readableDatabase
        return this
    }

    fun close() {
        dbHelper.close()
    }

    fun getLocation(number: String): String {
        var answer = ""
        try {
            val query = "SELECT GEOTEXT " +
                        "FROM GEODESCRIPTION " +
                        "WHERE IDP_END = " + number.toInt() +
                       " AND IDP_START = " + number.toInt()
            val cursor: Cursor = database!!.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val value: String = cursor.getString(0)
                    answer += if(cursor.isLast){
                        value
                    } else {
                        "$value, "
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()

        } catch (e: SQLException) {
            //handle
        }
        return answer
    }


    fun getNavigation(number1: String, number2: String): String {
        var answer = ""
        try {
            val query = "SELECT GEOTEXT " +
                    "FROM GEODESCRIPTION " +
                    "WHERE TYPE = \"Navigation\" " +
                    "AND IDP_START = " + number1.toInt() +
                   " AND IDP_END = " + number2.toInt()
            val cursor: Cursor = database!!.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val value: String = cursor.getString(0)
                    Log.d("db", value)
                    answer = value
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLException) {
            //handle
        }
        return answer
    }


    fun getTarget(number1: String): MutableList<String> {
        val answer: MutableList<String> = ArrayList()
        if(isNumber(number1)){
            try {
                val query = "SELECT IDP_END " +
                        "FROM GEODESCRIPTION " +
                        "WHERE TYPE = \"Navigation\" " +
                        "AND IDP_START = " + number1.toInt()
                val cursor: Cursor = database!!.rawQuery(query, null)
                if (cursor.moveToFirst()) {
                    do {
                        val value: String = cursor.getString(0)
                        Log.d("db", value)
                        answer.add(value)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            } catch (e: SQLException) {
                //handle
            }
        } else {
            Toast.makeText(context, "Wpisz liczbę", Toast.LENGTH_SHORT).show()
            answer.add("")
        }
        return answer
    }


    fun getPOI(): MutableMap<Int, MutableList<String>>{
        val answer = mutableMapOf<Int, MutableList<String>>()
            try {
                var key = 0
                val query = "SELECT IDP_START, GEOTEXT " +
                        "FROM GEODESCRIPTION " +
                        "WHERE TYPE = \"POI\" "
                val cursor: Cursor = database!!.rawQuery(query, null)
                if (cursor.moveToFirst()) {
                    do {
                        val id: String = cursor.getString(0)
                        val geotext: String = cursor.getString(1)
                        Log.d("db", geotext)
//                        println("rrrrrrrrr: $key, $value")
                        val poi = mutableListOf<String>()
                        poi.add(id)
                        poi.add(geotext)
                        answer[key] = poi
                        key += 1
                    } while (cursor.moveToNext())
                }
                cursor.close()
            } catch (e: SQLException) {
                //handle
            }

        return answer
    }


//powtórka
    private fun isNumber(s: String): Boolean {
        return try {
            s.toInt()
            true
        } catch (ex: NumberFormatException) {
            false
        }
    }

//    fun insertData(idstart: String, idend: String, text: String) {
//        val database = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put("idStart", idstart)
//        contentValues.put("idEnd", idend)
//        contentValues.put("uwaga", text)
//        val result = database?.insert("uwagi", null, contentValues)
//        if (result == (0).toLong()) {
//            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
//        }
//        else {
//            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
//        }
//    }
//    @SuppressLint("Range")
//    fun readData(): MutableList<String> {
//        val list: MutableList<String> = ArrayList()
////        val db = context.readableDatabase
//        val query = "Select * from uwagi"
//        val result: Cursor = database!!.rawQuery(query, null)
//        if (result != null) {
//            if (result.moveToFirst()) {
//                do {
//                    list.add(result.getString(result.getColumnIndex("idStart")))
//                    list.add(result.getString(result.getColumnIndex("idEnd")))
//                    list.add(result.getString(result.getColumnIndex("uwagi")))
//                }
//                while (result.moveToNext())
//            }
//        }
//        return list
//    }
}