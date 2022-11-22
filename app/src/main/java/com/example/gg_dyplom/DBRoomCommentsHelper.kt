package com.example.gg_dyplom

import androidx.room.*

@Entity
data class Comments(
    @PrimaryKey val id: Int,
//    @ColumnInfo(name = "id") val id: String?,
    @ColumnInfo(name = "lokalizacja") val lokalizacja: String?,
    @ColumnInfo(name = "cel") val cel: String?,
    @ColumnInfo(name = "komentarz") val komentarz: String?
)

@Dao
interface CommentsDao {
    @Query("SELECT * FROM comments")
    fun getAll(): List<Comments>

    @Query("SELECT * FROM comments WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Comments>

//    @Query("SELECT * FROM comments WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Comments
@Insert(onConflict = OnConflictStrategy.IGNORE)
suspend fun insert(id: Comments, lokalizacjia: Comments, cel: Comments, komentarz: Comments)

    @Insert
    fun insertAll(vararg users: Comments)

    @Delete
    fun delete(user: Comments)
}

@Database(entities = [Comments::class], version = 1)
abstract class CommentsDatabase : RoomDatabase() {
    abstract fun commentsDao(): CommentsDao
}

//annotation class DatabaseComents(val entities: Array<KClass<Comments>>, val version: Int)
