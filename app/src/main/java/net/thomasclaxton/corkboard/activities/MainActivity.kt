package net.thomasclaxton.corkboard.activities

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import net.thomasclaxton.corkboard.fragments.NewItemDialogFragment
import net.thomasclaxton.corkboard.adapters.NoteListAdapter
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.adapters.NoteListItemAdapter
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
  private lateinit var mTitleTextView: TextView
  private lateinit var mSearchView: SearchView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setupBottomAppBar()
    mAdapter = setupRecyclerView()
    setupViewModel(mAdapter)
    mSearchView = findViewById(R.id.searchView)
    setupSearchView()
  }

  override fun onResume() {
    super.onResume()

    // clear the focus of the search view
    mSearchView.clearFocus()
    setupViewModel(mAdapter)
  }

  private fun setupBottomAppBar() {
    mBottomAppBar = findViewById(R.id.bottomAppBar)

    mBottomAppBar.setNavigationOnClickListener {
      // TODO handle drawer press
    }

    mBottomAppBar.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.filter_notes -> {
          filterByType(Filter.NOTE)
          true
        }
        R.id.filter_lists -> {
          filterByType(Filter.NOTELIST)
          true
        }
        R.id.filter_reminders -> {
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
        val item = mAdapter.getNotes().removeAt(fromPosition)
        mAdapter.getNotes().add(toPosition, item)
        recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
        return true
      }

      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position: Int = viewHolder.adapterPosition
        val notableToDelete = ALL_NOTES[position]
        Snackbar.make(
          findViewById<CoordinatorLayout>(R.id.mainCoordinator),
          R.string.snackbar_delete,
          Snackbar.LENGTH_SHORT
        ).setAction(R.string.snackbar_undo) { mNotableViewModel.insert(notableToDelete) }
          .setAnchorView(R.id.bottomAppBar).show()
        mNotableViewModel.delete(notableToDelete.uid)
      }
    })

    itemTouchHelper.attachToRecyclerView(recyclerView)

    recyclerView.adapter = adapter
    recyclerView.isNestedScrollingEnabled = false
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

  private fun setupSearchView() {
    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    mSearchView.let {
      it.setOnFocusChangeListener { view, hasFocus ->
        if (!hasFocus) {
          val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
          inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
          it.findViewById<EditText>(R.id.search_src_text).isCursorVisible = false
        }
      }
      it.setSearchableInfo(searchManager.getSearchableInfo(componentName))
      it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
          mAdapter.filter(query)
          return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
          mAdapter.filter(newText)
          return true
        }
      })
    }
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
      R.menu.menu_main -> {
      }
      else -> supportActionBar?.title = getString(R.string.app_name)
    }

    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
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

  fun onCheckBoxClick(checkBoxView: View) {
    (checkBoxView as CheckBox).let { checkBoxView ->
      val noteListItemRecyclerView = checkBoxView.parent.parent as RecyclerView
      val holder = noteListItemRecyclerView.findViewHolderForAdapterPosition(checkBoxView.tag as Int) as NoteListItemAdapter.NoteListItemViewModeViewHolder
      val adapter = holder.adapter
      if (holder != null) {
        if (checkBoxView.isChecked) {
          adapter.handleCheckBoxClick(checkBoxView.tag as Int, true, holder)
        } else {
          adapter.handleCheckBoxClick(checkBoxView.tag as Int, false, holder)
        }
        // find the NoteList to which the item we checked belongs to
        val noteList = ALL_NOTES.filterIsInstance<NoteList>().firstOrNull { it.items == adapter.getNoteListItems() }

        noteList?.let { saveEditedNote(noteList) }
      }
    }
  }
}
