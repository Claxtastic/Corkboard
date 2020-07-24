package net.thomasclaxton.noter.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import net.thomasclaxton.noter.R
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
                    val newNoteIntent = Intent(context, CreateNoteActivity::class.java)
                    newNoteIntent.putExtra(getString(R.string.extras_request_code), 1)

                    // must call startActivityForResult() from activity, otherwise requestCode will change
                    activity!!.startActivityForResult(newNoteIntent, 1)
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