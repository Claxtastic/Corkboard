package net.thomasclaxton.noter

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note")
    fun getAll(): LiveData<List<Note>>

    @Insert
    suspend fun insert(note: Note)

    @Insert
    suspend fun insertAll(vararg notes: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note")
    fun deleteAll()
}
