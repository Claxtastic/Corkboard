package net.thomasclaxton.noter

import android.app.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText

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

    private fun saveNote() {
        val titleText = findViewById<EditText>(R.id.editTextNoteTitle).text.toString()
        val bodyText = findViewById<EditText>(R.id.editTextNoteBody).text.toString()

        if (intent.hasExtra("REQUESTCODE") && intent.getIntExtra("REQUESTCODE", 0) == 2) {
            val RESULT_EDIT: Int = 2
            Intent().let {
                val bundle = Bundle()
                val editedNote: Note = intent.getSerializableExtra("NOTE") as Note
                Log.d(TAG, ": editing ${editedNote.uid}");

                // TODO: Only change fields if they have been changed
                editedNote.title = titleText
                editedNote.body = bodyText

                bundle.putSerializable("NOTE", editedNote)
                it.putExtras(bundle)
                setResult(RESULT_EDIT, it)
            }
        }
        else if ( !(titleText.isEmpty() && bodyText.isEmpty()) ) {
            Intent().let {
                val bundle = Bundle()
                val newNote = Note(titleText, bodyText)
                Log.d(TAG, ": created ${newNote.uid}");
                bundle.putSerializable("NOTE", newNote)
                it.putExtras(bundle)
                setResult(Activity.RESULT_OK, it)
            }
        }
    }

    override fun onBackPressed() {
        // auto save not on back press if note is not empty
        saveNote()

        super.onBackPressed()
    }
}