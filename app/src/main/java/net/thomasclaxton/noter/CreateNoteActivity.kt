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
    }

    private fun saveNote() {
        val titleText = findViewById<EditText>(R.id.editTextNoteTitle).text.toString()
        val bodyText = findViewById<EditText>(R.id.editTextNoteBody).text.toString()

        if ( !(titleText.isEmpty() && bodyText.isEmpty()) ) {
            Intent().let {
                val bundle = Bundle()
                val newNote = Note(titleText, bodyText)
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