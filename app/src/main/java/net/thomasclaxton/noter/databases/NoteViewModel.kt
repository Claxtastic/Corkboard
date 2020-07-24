package net.thomasclaxton.noter.databases

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.thomasclaxton.noter.activities.MainActivity
import net.thomasclaxton.noter.models.Note

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>

    init {
        val notesDao = AppDatabase.getDatabase(application, viewModelScope).noteDao()
        repository = NoteRepository(notesDao)
        allNotes = repository.allNotes
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
        MainActivity.NOTES_ARRAY.add(note)
    }

    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
        MainActivity.NOTES_ARRAY.add(MainActivity.NOTES_ARRAY.indexOf(note), note)
    }

    fun delete(uid: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(uid)
        // TODO: Clean this up
        for (note: Note in MainActivity.NOTES_ARRAY) {
            if (note.uid == uid) MainActivity.NOTES_ARRAY.remove(note)
        }
    }

    fun getAll(): List<Note>? {
        return repository.getAll().value
    }
}