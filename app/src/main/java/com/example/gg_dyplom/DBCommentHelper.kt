package com.example.gg_dyplom
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast


class DatabaseOpenHelperCom(context: Context) :
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

    fun getWritable(): SQLiteDatabase? {
        return writableDatabase
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                LOC_COL + " TEXT," +
                TAR_COL + " TEXT," +
                COM_COL + " TEXT" + ")")

//        db.execSQL(query)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME;" );
        onCreate(db);
    }

    companion object {
        const val DB_NAME = "komentarze.db"
        const val DB_SUB_PATH = "/databases/$DB_NAME"
        private var APP_DATA_PATH = ""
        val TABLE_NAME = "comments"
        val ID_COL = "id"
        val LOC_COL = "lokalizacja"
        val TAR_COL = "cel"
        val COM_COL = "komentarz"
     }

    init {
        APP_DATA_PATH = context.applicationInfo.dataDir
        this.context = context
    }
}


class DatabaseCom(private val context: Context) {
    private var database: SQLiteDatabase? = null
    private val dbHelper: DatabaseOpenHelperCom = DatabaseOpenHelperCom(context)

    @Throws(SQLException::class)

    fun open(): DatabaseCom {
        dbHelper.openDataBase()
        dbHelper.close()
        database = dbHelper.readableDatabase
        return this
    }

    fun close() {
        dbHelper.close()
    }






    @SuppressLint("Range")
    fun getComments(): MutableMap<Int, List<String>> {
        val answer: MutableMap<Int, List<String>> = mutableMapOf()

            try {
                val query = "SELECT * FROM comments "
                val cursor: Cursor = database!!.rawQuery(query, null)
                if (cursor.moveToFirst()) {
                    do {
                        var id  = cursor.getInt(cursor.getColumnIndex("id"))
                        var loc = cursor.getString(cursor.getColumnIndex("lokalizacja"))
                        var cel = cursor.getString(cursor.getColumnIndex("cel"))
                        var kom = cursor.getString(cursor.getColumnIndex("komentarz"))

                        answer[id] = listOf(loc, cel, kom)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            } catch (e: SQLException) {
                //handle
            }

        return answer
    }




    fun readComment(numerId: Int): String {
        var answer = ""

        try {
            val query = "SELECT komentarz" +
                       " FROM comments " +
                        "WHERE id = " + numerId
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

    //powtórka
    private fun isNumber(s: String): Boolean {
        return try {
            s.toInt()
            true
        } catch (ex: NumberFormatException) {
            false
        }
    }

    fun insertData(idstart: String, idend: String, text: String) {
        val database = dbHelper.getWritable()
        val contentValues = ContentValues()
        contentValues.put("lokalizacja", idstart)
        contentValues.put("cel", idend)
        contentValues.put("komentarz", text)
        val result = database?.insert("comments", null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Dodanie komentarza nie powiodło się", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Dodano komentarz", Toast.LENGTH_SHORT).show()
        }
//        database?.execSQL("INSERT INTO comments ('lokalizacja', 'cel', 'komentarz') VALUES ($idstart, $idend, $text)")
        database?.close()
    }

    fun deleteRow(idx: Int){
        val database = dbHelper.getWritable()
        database?.execSQL("DELETE FROM comments WHERE id = $idx ;")
        Toast.makeText(context, "Usunięto", Toast.LENGTH_SHORT).show()
    }

    fun updateRow(idx: String, locx: String, celx: String, textx: String){
        val query = "UPDATE comments SET lokalizacja = '$locx', cel = '$celx', komentarz = '$textx' WHERE id = $idx;"
        val database = dbHelper.getWritable()
        database?.execSQL(query)
        Toast.makeText(context, "Edytowano", Toast.LENGTH_SHORT).show()
    }
}