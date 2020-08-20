package net.thomasclaxton.corkboard.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.thomasclaxton.corkboard.activities.MainActivity
import net.thomasclaxton.corkboard.databases.AppDatabase
import net.thomasclaxton.corkboard.repositories.NoteListRepository
import net.thomasclaxton.corkboard.models.NoteList

class NoteListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteListRepository
    val allNoteLists: LiveData<List<NoteList>>

    init {
        val noteListDao = AppDatabase.getDatabase(
            application,
            viewModelScope
        ).noteListDao()
        repository =
            NoteListRepository(noteListDao)

        allNoteLists = repository.allNoteLists
    }

    fun insert(noteList: NoteList) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(noteList)
        MainActivity.NOTES_ARRAY.add(noteList)
    }

    fun update(noteList: NoteList) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(noteList)
        MainActivity.NOTES_ARRAY.add(MainActivity.NOTES_ARRAY.indexOf(noteList), noteList)
    }

    fun delete(uid: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(uid)
        MainActivity.NOTES_ARRAY.filter { it.uid != uid }
    }

    fun getAll(): List<NoteList>? {
        return repository.getAll().value
    }
}