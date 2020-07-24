package net.thomasclaxton.noter.databases

import androidx.lifecycle.LiveData
import androidx.room.*
import net.thomasclaxton.noter.models.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note")
    fun getAll(): LiveData<List<Note>>

    @Insert
    suspend fun insert(note: Note)

    @Query("UPDATE Note SET title=:title, body=:body WHERE uid=:uid")
    suspend fun update(uid: String, title: String, body:String)

    @Insert
    suspend fun insertAll(vararg notes: Note)

    @Query("DELETE FROM Note WHERE uid=:uid")
    fun delete(uid: String)

    @Query("DELETE FROM Note")
    fun deleteAll()
}
