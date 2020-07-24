package net.thomasclaxton.noter.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import net.thomasclaxton.noter.activities.CreateNoteActivity
import java.lang.IllegalStateException

class NewItemDialogFragment : DialogFragment() {

    companion object {

        fun newInstance(): NewItemDialogFragment {
            return NewItemDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Choose type:")
                .setPositiveButton("Note")
                { _, _ ->
                    startActivityForResult(Intent(context, CreateNoteActivity::class.java), 1)
                }
                .setNeutralButton("List")
                { _, _ ->
                    Toast.makeText(context, "List", Toast.LENGTH_SHORT).show();
                }
                .setNegativeButton("Reminder")
                { _, _ ->
                    Toast.makeText(context, "Reminder", Toast.LENGTH_SHORT).show();
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}