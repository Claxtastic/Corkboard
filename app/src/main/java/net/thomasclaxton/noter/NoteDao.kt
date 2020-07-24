package net.thomasclaxton.noter

import androidx.lifecycle.LiveData
import androidx.room.*

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

//    @Query("SELECT * FROM Note WHERE ")

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note")
    fun deleteAll()
}
