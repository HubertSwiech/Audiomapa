package com.example.gg_dyplom

import androidx.room.*

@Entity
class Comments {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "lokalizacja")
    var lokalizacja: String? = null
    @ColumnInfo(name = "cel")
    var cel: String? = null
    @ColumnInfo(name = "komentarz")
    var komentarz: String? = null
}

@Dao
interface CommentsDao {
    @Query("SELECT * FROM comments")
    fun getAll(): List<Comments>
    @Query("SELECT * FROM comments WHERE id LIKE (:idx)")
    fun getComment(idx: Int): List<Comments>
    @Query("UPDATE comments SET lokalizacja = :loc, cel = :target, komentarz = :com WHERE id = :idx")
    fun updateComments(idx: Int, loc: String, target: String, com: String)
    @Insert
    fun insertComent(comment: Comments)
    @Query("DELETE FROM comments WHERE id = :idx")
    fun deleteComment(idx: Int)
}

@Database(entities = [Comments::class], version = 1)
abstract class CommentsDatabase : RoomDatabase() {
    abstract fun commentsDao(): CommentsDao
}

