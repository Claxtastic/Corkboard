package net.thomasclaxton.noter.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import net.thomasclaxton.noter.fragments.NewItemDialogFragment
import net.thomasclaxton.noter.models.Note
import net.thomasclaxton.noter.adapters.NoteListAdapter
import net.thomasclaxton.noter.R
import net.thomasclaxton.noter.databases.NoteViewModel

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    companion object {
        /**
         * The master list which contains all notes currently in database.
         */
        // TODO: change to val
        var NOTES_ARRAY: ArrayList<Note> = ArrayList<Note>()

        /**
         * Menu for this activity. Changes depending on whether a note is selected.
         */
        var currentMenu: Int = R.menu.menu_main
    }

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var mAdapter: NoteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        mAdapter = setupRecyclerView()
        setupViewModel(mAdapter)
    }

    private fun setupRecyclerView(): NoteListAdapter {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = NoteListAdapter(this)

        // Callback for swipe to delete on RecyclerView item
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position: Int = viewHolder.adapterPosition
                val swipedNote = NOTES_ARRAY[position]
                noteViewModel.delete(swipedNote.uid)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)

        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = object : GridLayoutManager(this, 2) {
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                // attach swipe helper to RecyclerView only when the layout is completed
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }
        }

        return adapter
    }

    private fun setupViewModel(adapter: NoteListAdapter) {
        noteViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(this!!.application)).get(
            NoteViewModel::class.java)
        noteViewModel.allNotes.observe(this, Observer { notes ->
            notes?.let { adapter.setNotes(notes) }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            // new note was created
            saveNewNote(data)
        } else if (requestCode == 2) {
            // existing note was edited
            saveEditedNote(data)
        }
    }

    private fun saveNewNote(data: Intent?) {
        if (data?.extras != null) {
            data?.extras!!.getSerializable(getString(R.string.extras_note))
            val noteBundle: Bundle = data?.extras!!
            noteBundle.getSerializable(getString(R.string.extras_note)).let {
                noteViewModel.insert(it as Note)
            }
        }
    }

    private fun saveEditedNote(data: Intent?) {
        if (data?.extras!!.getSerializable(getString(R.string.extras_note)) != null) {
            val editedNodeBundle: Bundle = data.extras!!
            editedNodeBundle.getSerializable(getString(R.string.extras_note)).let {
                noteViewModel.update(it as Note)
            }
        } else {
            // all the fields of this note were backspaced
            data.getStringExtra("UID")!!.let {
                noteViewModel.delete(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(currentMenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (currentMenu == R.menu.menu_select) {
            Log.d(TAG, ": select menu is active");
            // the selection menu is active, so 1 >= note must be selected
            for (note: Note in NOTES_ARRAY) {
                if (note.isSelected) {
                    note.isSelected = false
                    mAdapter.notifyDataSetChanged()
                }
            }
            currentMenu = R.menu.menu_main
            invalidateOptionsMenu()
        } else {
            super.onBackPressed()
        }
    }

    fun onFabClick(fabView: View) {
        // Dialog: What kind of note?
        //     - Note
        //     - List
        //     - Reminder
        // (these can be converted in the editor as well)
        val dialogFragment =
            NewItemDialogFragment.newInstance()
        val fragmentManager = supportFragmentManager.beginTransaction()

        dialogFragment.show(fragmentManager, "dialog")
    }
}
