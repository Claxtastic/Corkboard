package net.thomasclaxton.corkboard.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import net.thomasclaxton.corkboard.adapters.NoteListAdapter
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.interfaces.Notable
import net.thomasclaxton.corkboard.models.Note
import net.thomasclaxton.corkboard.models.NoteList
import net.thomasclaxton.corkboard.viewmodels.NotableViewModel
import java.io.Serializable

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    companion object {
        /** The master list which contains all notes currently in database. **/
        var ALL_NOTES: ArrayList<Notable> = ArrayList()

        /** Lists currently visible via the set filter. **/
        var FILTERED_NOTES: ArrayList<Notable> = ArrayList()

        /** Menu for this activity. Changes depending on whether a note is selected. **/
        var currentMenu: Int = R.menu.menu_main
    }

    enum class Filter {
        NOTE, NOTELIST, REMINDER, NONE
    }

    private lateinit var mBottomAppBar: BottomAppBar
    private lateinit var mAdapter: NoteListAdapter
    private lateinit var mNotableViewModel: NotableViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        setupBottomAppBar()
        mAdapter = setupRecyclerView()
        setupViewModel(mAdapter)
    }

    private fun setupBottomAppBar() {
        mBottomAppBar = findViewById(R.id.bottomAppBar)

        mBottomAppBar.setNavigationOnClickListener {
            // TODO handle drawer press
        }

        mBottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                // TODO change appbar title with each filter type accordingly
                R.id.filter_notes -> {
                    Toast.makeText(applicationContext, "Showing notes only", Toast.LENGTH_SHORT).show()
                    filterByType(Filter.NOTE)
                    true
                }
                R.id.filter_lists -> {
                    Toast.makeText(applicationContext, "Showing lists only", Toast.LENGTH_SHORT).show()
                    filterByType(Filter.NOTELIST)
                    true
                }
                R.id.filter_reminders -> {
                    Toast.makeText(applicationContext, "Showing reminders only", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.filter_clear -> {
                    filterByType(Filter.NONE)
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
                val item = ALL_NOTES.removeAt(fromPosition)
                ALL_NOTES.add(toPosition, item)
                recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position: Int = viewHolder.adapterPosition
                mNotableViewModel.delete(ALL_NOTES[position].uid)
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
        mNotableViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(this.application)).get(
            NotableViewModel::class.java
        )
        mNotableViewModel.getAll().observe(
            this,
            Observer { notes ->
                notes?.let {
                    ALL_NOTES = notes as ArrayList<Notable>
                    adapter.setNotes(ALL_NOTES)
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val serializable: Serializable? = getExtraAsSerializable(data)
            if (serializable != null) {
                if (requestCode == 1) {
                    // new note was created
                    saveNewNote(serializable as Notable)
                } else if (requestCode == 2) {
                    // existing note was edited
                    saveEditedNote(serializable)
                }
            }
        }
    }

    private fun getExtraAsSerializable(data: Intent?): Serializable? {
        return if (data?.extras != null) {
            data.extras!!.getSerializable(getString(R.string.extras_note))
            val notableBundle: Bundle = data.extras!!

            return if (notableBundle.getSerializable(getString(R.string.extras_note)) != null) {
                notableBundle.getSerializable(getString(R.string.extras_note))
            } else {
                notableBundle.getSerializable(getString(R.string.extras_uid))
            }
        } else {
            null
        }
    }

    private fun saveNewNote(notable: Notable) {
        mNotableViewModel.insert(notable)
    }

    private fun saveEditedNote(serializable: Serializable) {
        if (serializable is Notable) {
            mNotableViewModel.update(serializable)
        } else {
            // all the fields of this note were backspaced
            mNotableViewModel.delete(serializable as String)
        }
    }

    private fun filterByType(filter: Filter) {
        FILTERED_NOTES.clear()
        FILTERED_NOTES.addAll(ALL_NOTES)
        when (filter) {
            Filter.NOTE -> mAdapter.setNotes(FILTERED_NOTES.filterIsInstance<Note>())
            Filter.NOTELIST -> mAdapter.setNotes(FILTERED_NOTES.filterIsInstance<NoteList>())
            Filter.REMINDER -> null
            Filter.NONE -> mAdapter.setNotes(ALL_NOTES)
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
        ALL_NOTES
            .filter { it.isSelected }
            .forEach {
                mNotableViewModel.delete(it.uid)
            }
        currentMenu = R.menu.menu_main
        mAdapter.undoSelections()
        invalidateOptionsMenu()
    }
}
