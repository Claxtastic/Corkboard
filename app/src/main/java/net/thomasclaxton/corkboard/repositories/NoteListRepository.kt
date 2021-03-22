package net.thomasclaxton.corkboard.repositories

import androidx.lifecycle.LiveData
import net.thomasclaxton.corkboard.interfaces.NoteListDao
import net.thomasclaxton.corkboard.models.NoteList
import net.thomasclaxton.corkboard.models.NoteListItem

class NoteListRepository(private val noteListDao: NoteListDao) {

  fun insert(noteList: NoteList) {
    noteListDao.insert(noteList)
  }

  fun update(noteList: NoteList) {
    val uid: String = noteList.uid
    val newTitle: String = noteList.title
    val newItems: ArrayList<NoteListItem> = noteList.items
    noteListDao.update(uid, newTitle, newItems)
  }

  fun delete(uid: String) {
    noteListDao.delete(uid)
  }

  fun getAll(): LiveData<List<NoteList>> {
    return noteListDao.getAll()
  }
}
