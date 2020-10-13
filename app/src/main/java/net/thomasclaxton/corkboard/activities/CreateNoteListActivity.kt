package net.thomasclaxton.corkboard.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.adapters.NoteListItemAdapter
import net.thomasclaxton.corkboard.models.NoteList
import net.thomasclaxton.corkboard.models.NoteListItem

private const val TAG = "CreateNoteListActivity"

class CreateNoteListActivity : AppCompatActivity() {

    private lateinit var mAdapter: NoteListItemAdapter
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note_list)

        mAdapter = setupRecyclerView()
        mAdapter.addItem()
        checkExtras()
    }

    private fun checkExtras() {
        if (intent.hasExtra(getString(R.string.extras_note))) {
            (intent.getSerializableExtra(getString(R.string.extras_note)) as NoteList).let {
                findViewById<EditText>(R.id.editTextNoteListTitle).setText(it.title)
                val copiedNoteListItems: ArrayList<NoteListItem> = ArrayList()
                copiedNoteListItems.addAll(it.items)
                mAdapter.setNoteListItems(copiedNoteListItems)
            }
        }
    }

    private fun setupRecyclerView(): NoteListItemAdapter {
        mRecyclerView = findViewById(R.id.recyclerViewListItems)
        val adapter = NoteListItemAdapter(this, NoteListItemAdapter.Mode.CREATE)

        mRecyclerView.adapter = adapter
        mRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        return adapter
    }

    fun onFabClick(view: View) {
        mAdapter.addItem()
    }

    fun onCheckBoxClick(checkBoxView: View) {
        (checkBoxView as CheckBox).let {
            if (checkBoxView.isChecked) {
                Toast.makeText(applicationContext, "Is checked", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Is unchecked", Toast.LENGTH_SHORT).show()
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

    private fun saveEditedNoteList(titleText: String, items: ArrayList<NoteListItem>) {
        if (!noteListIsEmpty(titleText, items)) {
            Intent().let {
                val bundle = Bundle()
                val editedNoteList: NoteList = intent.getSerializableExtra("NOTE") as NoteList

                if (titleText != editedNoteList.title || items != editedNoteList.items) {
                    editedNoteList.title = titleText
                    editedNoteList.items = items

                    bundle.putSerializable(getString(R.string.extras_note), editedNoteList)
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
}
