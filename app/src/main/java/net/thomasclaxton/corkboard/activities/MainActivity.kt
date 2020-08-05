package net.thomasclaxton.corkboard.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import net.thomasclaxton.corkboard.fragments.NewItemDialogFragment
import net.thomasclaxton.corkboard.models.Note
import net.thomasclaxton.corkboard.adapters.NoteListAdapter
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.databases.NoteViewModel

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    companion object {
        /** The master list which contains all notes currently in database. **/
        var NOTES_ARRAY: ArrayList<Note> = ArrayList()

        /** Menu for this activity. Changes depending on whether a note is selected. **/
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
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun isLongPressDragEnabled() = true
            override fun isItemViewSwipeEnabled() = true

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = UP or DOWN or START or END
                val swipeFlags = LEFT or RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                val item = NOTES_ARRAY.removeAt(fromPosition)
                NOTES_ARRAY.add(toPosition, item)
                recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position: Int = viewHolder.adapterPosition
                val swipedNote = NOTES_ARRAY[position]
                noteViewModel.delete(swipedNote.uid)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = object : StaggeredGridLayoutManager(2, 1) {
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                // attach swipe helper to RecyclerView only when the layout is completed
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }
        }

        return adapter
    }

    private fun setupViewModel(adapter: NoteListAdapter) {
        noteViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(this.application)).get(
            NoteViewModel::class.java
        )

        noteViewModel.allNotes.observe(
            this,
            Observer { notes ->
                notes?.let { adapter.setNotes(notes) }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                // new note was created
                saveNewNote(data)
            } else if (requestCode == 2) {
                // existing note was edited
                saveEditedNote(data)
            }
        }
    }

    private fun saveNewNote(data: Intent?) {
        if (data?.extras != null) {
            data.extras!!.getSerializable(getString(R.string.extras_note))
            val noteBundle: Bundle = data.extras!!
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
            data.getStringExtra(getString(R.string.extras_uid))!!.let {
                noteViewModel.delete(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(currentMenu, menu)
        when (currentMenu) {
            R.menu.menu_select -> supportActionBar?.title = ""
            else -> supportActionBar?.title = getString(R.string.app_name)
        }

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
            // the selection menu is active, so 1 >= note must be selected
            currentMenu = R.menu.menu_main
            mAdapter.undoSelections()
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
        if (currentMenu == R.menu.menu_select) {
            // undo selection because we are creating a new note
            currentMenu = R.menu.menu_main
            mAdapter.undoSelections()
            invalidateOptionsMenu()
        }

        val dialogFragment =
            NewItemDialogFragment.newInstance()
        val fragmentManager = supportFragmentManager.beginTransaction()

        dialogFragment.show(fragmentManager, "dialog")
    }

    fun onCloseClick(view: MenuItem) {
        currentMenu = R.menu.menu_main
        mAdapter.undoSelections()
        invalidateOptionsMenu()
    }

    fun onDeleteClick(view: MenuItem) {
        NOTES_ARRAY
            .filter { it.isSelected }
            .forEach {
                noteViewModel.delete(it.uid)
            }
        currentMenu = R.menu.menu_main
        mAdapter.undoSelections()
        invalidateOptionsMenu()
    }
}
