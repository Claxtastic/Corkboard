package net.thomasclaxton.noter

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<Note>> = noteDao.getAll()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }
}