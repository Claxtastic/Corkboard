package net.thomasclaxton.corkboard.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.activities.CreateNoteActivity
import java.lang.IllegalStateException

class NewItemDialogFragment : DialogFragment() {

    companion object {

        fun newInstance(): NewItemDialogFragment {
            return NewItemDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it, R.style.AlertDialogTheme)
            builder.setMessage("Choose type:")
                .setPositiveButton("Note") { _, _ ->
                    val newNoteIntent = Intent(context, CreateNoteActivity::class.java)
                    newNoteIntent.putExtra(getString(R.string.extras_request_code), 1)

                    // must call startActivityForResult() from activity, otherwise requestCode will change
                    activity!!.startActivityForResult(newNoteIntent, 1)
                }
                .setNeutralButton("List") { _, _ ->
                    Toast.makeText(context, "List", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Reminder") { _, _ ->
                    Toast.makeText(context, "Reminder", Toast.LENGTH_SHORT).show()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
