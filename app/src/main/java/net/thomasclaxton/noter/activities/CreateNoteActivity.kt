package net.thomasclaxton.noter.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import net.thomasclaxton.noter.models.Note
import net.thomasclaxton.noter.R

private const val RESULT_EDIT: Int = 2
private const val TAG = "CreateNoteActivity";

class CreateNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        checkExtras()
    }

    private fun checkExtras() {
        if (intent.hasExtra("NOTE")) {
            // we are editing or viewing an already created note
            (intent.getSerializableExtra("NOTE") as Note).let {
                findViewById<EditText>(R.id.editTextNoteTitle).setText(it.title)
                findViewById<EditText>(R.id.editTextNoteBody).setText(it.body)
            }
        }
    }

    override fun onBackPressed() {
        // auto save not on back press if note is not empty
        saveNote()

        super.onBackPressed()
    }

    private fun saveNote() {
        val titleText = findViewById<EditText>(R.id.editTextNoteTitle).text.toString()
        val bodyText = findViewById<EditText>(R.id.editTextNoteBody).text.toString()

        intent.getIntExtra("REQUEST_CODE", 0).let {
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

                bundle.putSerializable("NOTE", newNote)
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
                val editedNote: Note = intent.getSerializableExtra("NOTE") as Note

                // TODO: Only change fields if they have been changed
                editedNote.title = titleText
                editedNote.body = bodyText

                bundle.putSerializable("NOTE", editedNote)
                it.putExtras(bundle)
                setResult(RESULT_EDIT, it)
            }
        } else {
            // user backspaced all the fields of this note
            Intent().let {
                val deletedNote: Note = intent.getSerializableExtra("NOTE") as Note
                it.putExtra("UID", deletedNote.uid)
                setResult(Activity.RESULT_OK, it)
            }
        }
    }

    private fun noteIsEmpty(titleText: String, bodyText: String): Boolean {
        return titleText.isEmpty() && bodyText.isEmpty()
    }
}