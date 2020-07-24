package net.thomasclaxton.noter

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<Note>> = noteDao.getAll()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        val uid: String = note.uid
        val newTitle: String = note.title
        val newBody: String = note.body
        noteDao.update(uid, newTitle, newBody)
    }

    fun delete(uid: String) {
        noteDao.delete(uid)
    }
}