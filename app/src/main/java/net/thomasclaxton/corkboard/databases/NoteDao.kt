package net.thomasclaxton.corkboard.databases

import androidx.lifecycle.LiveData
import androidx.room.*
import net.thomasclaxton.corkboard.models.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note")
    fun getAll(): LiveData<List<Note>>

    @Insert
    fun insert(note: Note)

    @Query("UPDATE Note SET title=:title, body=:body WHERE uid=:uid")
    fun update(uid: String, title: String, body: String)

    @Insert
    fun insertAll(vararg notes: Note)

    @Query("DELETE FROM Note WHERE uid=:uid")
    fun delete(uid: String)

    @Query("DELETE FROM Note")
    fun deleteAll()
}
