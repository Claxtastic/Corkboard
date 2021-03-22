package net.thomasclaxton.corkboard.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_create_note_list.*
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.adapters.NoteListItemAdapter
import net.thomasclaxton.corkboard.models.NoteList
import net.thomasclaxton.corkboard.models.NoteListItem

private const val TAG = "CreateNoteListActivity"

class CreateNoteListActivity : AppCompatActivity() {

  private lateinit var mAdapter: NoteListItemAdapter
  private lateinit var mRecyclerView: RecyclerView
  private lateinit var mOriginalNoteList: NoteList

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_create_note_list)

    mAdapter = setupRecyclerView()
    mAdapter.addItem()
    checkExtras()
  }

  private fun checkExtras() {
    if (intent.hasExtra(getString(R.string.extras_note))) {
      (intent.getSerializableExtra(getString(R.string.extras_note)) as NoteList).let { noteList ->
        findViewById<EditText>(R.id.editTextNoteListTitle).setText(noteList.title)
        // save passed NoteList to compare with edited notes
        mOriginalNoteList = noteList
        // recreate NoteListItems in new NoteList to populate UI
        val itemsToBeEdited: ArrayList<NoteListItem> = ArrayList()
        noteList.items.forEach { itemToBeEdited ->
          itemsToBeEdited.add(NoteListItem(itemToBeEdited.item, itemToBeEdited.isChecked))
        }
        mAdapter.setNoteListItems(itemsToBeEdited)
      }
    }
  }

  private fun setupRecyclerView(): NoteListItemAdapter {
    mRecyclerView = findViewById(R.id.recyclerViewListItems)
    val adapter = NoteListItemAdapter(this, NoteListItemAdapter.Mode.CREATE)

    mRecyclerView.adapter = adapter
    val noteListLayoutManager = LinearLayoutManager(applicationContext)
    mRecyclerView.layoutManager = noteListLayoutManager

    return adapter
  }

  fun onFabClick(view: View) {
    mAdapter.addItem()
  }

  fun onRemoveClick(view: View) {
    mAdapter.removeItem(view.tag as Int)
  }

  fun onCheckBoxClick(checkBoxView: View) {
    (checkBoxView as CheckBox).let {
      val noteListItemPos = it.tag as Int
      val holder: RecyclerView.ViewHolder? = recyclerViewListItems.findViewHolderForAdapterPosition(noteListItemPos)
      if (holder != null) {
        if (it.isChecked) {
          mAdapter.handleCheckBoxClick(noteListItemPos, true, holder)
        } else {
          mAdapter.handleCheckBoxClick(noteListItemPos, false, holder)
        }
      }
    }
  }

  override fun onBackPressed() {
    // auto save not on back press if note is not empty
    saveNoteList()

    super.onBackPressed()
  }

  private fun saveNoteList() {
    val titleText = findViewById<EditText>(R.id.editTextNoteListTitle).text.toString()
    val items: ArrayList<NoteListItem> = mAdapter.getNoteListItems()

    intent.getIntExtra(getString(R.string.extras_request_code), 0).let {
      if (it == 1) {
        // creating a new note
        saveNewNoteList(titleText, items)
      } else if (it == 2) {
        // editing an existing note
        saveEditedNoteList(titleText, items)
      }
    }
  }

  private fun saveNewNoteList(titleText: String, items: ArrayList<NoteListItem>) {
    if (!noteListIsEmpty(titleText, items)) {
      Intent().let {
        val bundle = Bundle()
        val newNoteList = NoteList(titleText, items)

        bundle.putSerializable(getString(R.string.extras_note), newNoteList)
        it.putExtras(bundle)
        setResult(Activity.RESULT_OK, it)
      }
    } else {
      setResult(Activity.RESULT_OK)
    }
  }

  private fun saveEditedNoteList(newTitleText: String, newItems: ArrayList<NoteListItem>) {
    if (!noteListIsEmpty(newTitleText, newItems)) {
      Intent().let {
        val bundle = Bundle()

        if (newTitleText != mOriginalNoteList.title || areItemsDifferent(mOriginalNoteList.items, newItems)) {
          mOriginalNoteList.title = newTitleText
          mOriginalNoteList.items = newItems

          bundle.putSerializable(getString(R.string.extras_note), mOriginalNoteList)
          it.putExtras(bundle)
          setResult(Activity.RESULT_OK, it)
        } else {
          setResult(Activity.RESULT_CANCELED, it)
        }
      }
    } else {
      Intent().let {
        val deletedNoteList: NoteList = intent.getSerializableExtra(getString(R.string.extras_note)) as NoteList
        it.putExtra(getString(R.string.extras_uid), deletedNoteList.uid)
        setResult(Activity.RESULT_OK, it)
      }
    }
  }

  private fun noteListIsEmpty(titleText: String, items: ArrayList<NoteListItem>): Boolean {
    return titleText.isEmpty() || items.isEmpty()
  }

  // TODO: Make this Comparable<NoteListItem> in NoteListItem
  private fun areItemsDifferent(original: ArrayList<NoteListItem>, edited: ArrayList<NoteListItem>): Boolean {
    if (original.size != edited.size) return true
    for ((index, item) in original.withIndex()) {
      if (item.item != edited[index].item) return true
      else if (item.isChecked != edited[index].isChecked) return true
    }
    return false
  }
}
