package net.thomasclaxton.corkboard.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.thomasclaxton.corkboard.activities.MainActivity
import net.thomasclaxton.corkboard.databases.AppDatabase
import net.thomasclaxton.corkboard.interfaces.Notable
import net.thomasclaxton.corkboard.models.Note
import net.thomasclaxton.corkboard.models.NoteList
import net.thomasclaxton.corkboard.repositories.NoteListRepository
import net.thomasclaxton.corkboard.repositories.NoteRepository

class NotableViewModel(application: Application) : AndroidViewModel(application) {

    private val noteRepository: NoteRepository
    private val noteListRepository: NoteListRepository

    init {
        val noteDao = AppDatabase.getDatabase(
            application,
            viewModelScope
        ).noteDao()
        val noteListDao = AppDatabase.getDatabase(
            application,
            viewModelScope
        ).noteListDao()

        noteRepository = NoteRepository(noteDao)
        noteListRepository = NoteListRepository(noteListDao)
    }

    /** Launching a new coroutine to insert the data in a non-blocking way **/
    fun insert(notable: Notable) = viewModelScope.launch(Dispatchers.IO) {
        when (notable) {
            is Note -> noteRepository.insert(notable)
            is NoteList -> noteListRepository.insert(notable)
        }
        MainActivity.ALL_NOTES.add(notable)
    }

    fun update(notable: Notable) = viewModelScope.launch(Dispatchers.IO) {
        when (notable) {
            is Note -> noteRepository.update(notable)
            is NoteList -> noteListRepository.update(notable)
        }
        MainActivity.ALL_NOTES.add(MainActivity.ALL_NOTES.indexOf(notable), notable)
    }

    fun delete(uid: String) = viewModelScope.launch(Dispatchers.IO) {
        when (MainActivity.ALL_NOTES.filter { it.uid == uid }[0]) {
            is Note -> noteRepository.delete(uid)
            is NoteList -> noteListRepository.delete(uid)
        }
        MainActivity.ALL_NOTES.filter { it.uid != uid }
    }

    fun getAll(): LiveData<List<Notable>> {
        val allNotes: LiveData<List<Note>> = noteRepository.getAll()
        val allNoteLists: LiveData<List<NoteList>> = noteListRepository.getAll()
        val allNotables = MediatorLiveData<List<Notable>>()

        allNotables.addSource(allNotes) { value ->
            if (value != null)
                allNotables.value = combineNotables(allNotes, allNoteLists)
        }
        allNotables.addSource(allNoteLists) { value ->
            if (value != null)
                allNotables.value = combineNotables(allNotes, allNoteLists)
        }
        return allNotables
    }

    private fun combineNotables(notes: LiveData<List<Note>>, noteLists: LiveData<List<NoteList>>): List<Notable> {
        val result: ArrayList<Notable> = ArrayList()
        val notesValue = notes.value
        val noteListsValue = noteLists.value

        if (notesValue != null) result.addAll(notesValue)
        if (noteListsValue != null) result.addAll(noteListsValue)
        return result
    }
}
