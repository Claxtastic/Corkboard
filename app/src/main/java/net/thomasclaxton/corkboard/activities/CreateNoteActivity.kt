package net.thomasclaxton.corkboard.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import net.thomasclaxton.corkboard.models.Note
import net.thomasclaxton.corkboard.R

private const val TAG = "CreateNoteActivity"

class CreateNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        checkExtras()
    }

    private fun checkExtras() {
        if (intent.hasExtra(getString(R.string.extras_note))) {
            // we are editing or viewing an already created note
            (intent.getSerializableExtra(getString(R.string.extras_note)) as Note).let {
                findViewById<EditText>(R.id.editTextNoteListTitle).setText(it.title)
                findViewById<EditText>(R.id.editTextNoteListItem).setText(it.body)
            }
        }
    }

    override fun onBackPressed() {
        // auto save not on back press if note is not empty
        saveNote()

        super.onBackPressed()
    }

    private fun saveNote() {
        val titleText = findViewById<EditText>(R.id.editTextNoteListTitle).text.toString()
        val bodyText = findViewById<EditText>(R.id.editTextNoteListItem).text.toString()

        intent.getIntExtra(getString(R.string.extras_request_code), 0).let {
            if (it == 1) {
                // creating a new note
                saveNewNote(titleText, bodyText)
            } else if (it == 2) {
                // editing an existing note
                saveEditedNote(titleText, bodyText)
            }
        }
    }

    private fun saveNewNote(titleText: String, bodyText: String) {
        if (!noteIsEmpty(titleText, bodyText)) {
            Intent().let {
                val bundle = Bundle()
                val newNote = Note(titleText, bodyText)

                bundle.putSerializable(getString(R.string.extras_note), newNote)
                it.putExtras(bundle)
                setResult(Activity.RESULT_OK, it)
            }
        } else {
            // the fields of this note are empty
            setResult(Activity.RESULT_OK)
        }
    }

    private fun saveEditedNote(titleText: String, bodyText: String) {
        if (!noteIsEmpty(titleText, bodyText)) {
            Intent().let {
                val bundle = Bundle()
                val editedNote: Note = intent.getSerializableExtra(getString(R.string.extras_note)) as Note

                if (titleText != editedNote.title || bodyText != editedNote.body) {
                    // the note's contents were edited
                    editedNote.title = titleText
                    editedNote.body = bodyText

                    bundle.putSerializable(getString(R.string.extras_note), editedNote)
                    it.putExtras(bundle)
                    setResult(Activity.RESULT_OK, it)
                } else {
                    // nothing was changed
                    setResult(Activity.RESULT_CANCELED, it)
                }
            }
        } else {
            // user backspaced all the fields of this note
            Intent().let {
                val deletedNote: Note = intent.getSerializableExtra(getString(R.string.extras_note)) as Note
                it.putExtra(getString(R.string.extras_uid), deletedNote.uid)
                setResult(Activity.RESULT_OK, it)
            }
        }
    }

    private fun noteIsEmpty(titleText: String, bodyText: String): Boolean {
        return titleText.isEmpty() && bodyText.isEmpty()
    }
}
