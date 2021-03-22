package net.thomasclaxton.corkboard.interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import net.thomasclaxton.corkboard.models.NoteList
import net.thomasclaxton.corkboard.models.NoteListItem

@Dao
interface NoteListDao {
  @Query("SELECT * FROM NoteList")
  fun getAll(): LiveData<List<NoteList>>

  @Insert
  fun insert(noteList: NoteList)

  @Query("UPDATE NoteList SET title=:title, items=:items WHERE uid=:uid")
  fun update(uid: String, title: String, items: ArrayList<NoteListItem>)

  @Insert
  fun insertAll(vararg noteLists: NoteList)

  @Query("DELETE FROM NoteList WHERE uid=:uid")
  fun delete(uid: String)

  @Query("DELETE FROM NoteList")
  fun deleteAll()
}
