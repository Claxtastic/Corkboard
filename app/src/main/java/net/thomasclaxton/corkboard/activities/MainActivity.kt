package net.thomasclaxton.corkboard.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import net.thomasclaxton.corkboard.fragments.NewItemDialogFragment
import net.thomasclaxton.corkboard.models.Note
import net.thomasclaxton.corkboard.adapters.NoteListAdapter
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.interfaces.Notable
import net.thomasclaxton.corkboard.models.NoteList
import net.thomasclaxton.corkboard.viewmodels.NoteListViewModel
import net.thomasclaxton.corkboard.viewmodels.NoteViewModel

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    companion object {
        /** The master list which contains all notes currently in database. **/
        var NOTES_ARRAY: ArrayList<Notable> = ArrayList()

        /** Menu for this activity. Changes depending on whether a note is selected. **/
        var currentMenu: Int = R.menu.menu_main
    }

    private lateinit var mBottomAppBar: BottomAppBar
    private lateinit var mAdapter: NoteListAdapter
    private lateinit var mNoteViewModel: NoteViewModel
    private lateinit var mNoteListViewModel: NoteListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        setupBottomAppBar()
        mAdapter = setupRecyclerView()
        setupViewModels(mAdapter)
    }

    private fun setupBottomAppBar() {
        mBottomAppBar = findViewById(R.id.bottomAppBar)

        mBottomAppBar.setNavigationOnClickListener {
            // handle drawer press
        }

        mBottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.navigation_notes -> {
                    Toast.makeText(applicationContext, "Showing notes only", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_lists -> {
                    Toast.makeText(applicationContext, "Showing lists only", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_reminders -> {
                    Toast.makeText(applicationContext, "Showing reminders only", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            true
        }
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
                when (val swipedNote = NOTES_ARRAY[position]) {
                    is Note -> mNoteViewModel.delete(swipedNote.uid)
                    is NoteList -> mNoteListViewModel.delete(swipedNote.uid)
                }
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

    private fun setupViewModels(adapter: NoteListAdapter) {
        mNoteViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(this.application)).get(
            NoteViewModel::class.java
        )

        mNoteListViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(this.application)).get(
            NoteListViewModel::class.java
        )

        mNoteViewModel.allNotes.observe(
            this,
            Observer { notes ->
                notes?.let {
                    adapter.setNotes(notes)
                }
            }
        )

        mNoteListViewModel.allNoteLists.observe(
            this,
            Observer { noteLists ->
                noteLists?.let {
                    NOTES_ARRAY.addAll(noteLists)
                    adapter.setNotes(NOTES_ARRAY)
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val notable: Notable? = getExtraAsNotable(data)
            if (requestCode == 1) {
                // new note was created
                when (notable) {
                    null -> null
                    is Note -> saveNewNote(notable)
                    is NoteList -> saveNewNoteList(notable)
                }
            } else if (requestCode == 2) {
                // existing note was edited
                when (notable) {
                    is Note -> saveEditedNote(notable, data)
                    is NoteList -> saveEditedNoteList(notable, data)
                }
            }
        }
    }

    private fun getExtraAsNotable(data: Intent?): Notable? {
        return if (data?.extras != null) {
            data.extras!!.getSerializable(getString(R.string.extras_note))
            val notableBundle: Bundle = data.extras!!

            notableBundle.getSerializable(getString(R.string.extras_note)) as Notable
        } else {
            null
        }
    }

    private fun saveNewNote(note: Note) {
        mNoteViewModel.insert(note)
    }

    private fun saveNewNoteList(noteList: NoteList) {
        mNoteListViewModel.insert(noteList)
    }

    private fun saveEditedNote(note: Note?, data: Intent?) {
        if (note != null) {
            mNoteViewModel.update(note)
        } else {
            // all the fields of this note were backspaced
            mNoteViewModel.delete(data?.extras?.getSerializable(getString(R.string.extras_uid)) as String)
        }
    }

    private fun saveEditedNoteList(noteList: NoteList?, data: Intent?) {
        if (noteList != null) {
            mNoteListViewModel.update(noteList)
        } else {
            mNoteListViewModel.delete(data?.extras?.getSerializable(getString(R.string.extras_uid)) as String)
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
                mNoteViewModel.delete(it.uid)
            }
        currentMenu = R.menu.menu_main
        mAdapter.undoSelections()
        invalidateOptionsMenu()
    }
}
