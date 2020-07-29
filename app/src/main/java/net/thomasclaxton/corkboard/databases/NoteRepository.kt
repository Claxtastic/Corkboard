package net.thomasclaxton.corkboard.databases

import androidx.lifecycle.LiveData
import net.thomasclaxton.corkboard.models.Note

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<Note>> = noteDao.getAll()

    fun insert(note: Note) {
        noteDao.insert(note)
    }

    fun update(note: Note) {
        val uid: String = note.uid
        val newTitle: String = note.title
        val newBody: String = note.body
        noteDao.update(uid, newTitle, newBody)
    }

    fun delete(uid: String) {
        noteDao.delete(uid)
    }

    fun getAll(): LiveData<List<Note>> {
        return noteDao.getAll()
    }
}
