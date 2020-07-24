package net.thomasclaxton.noter.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.thomasclaxton.noter.R
import net.thomasclaxton.noter.activities.CreateNoteActivity
import net.thomasclaxton.noter.models.Note
import net.thomasclaxton.noter.activities.MainActivity

private const val TAG = "NoteListAdapter"

class NoteListAdapter internal constructor (context: Context)
    : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mNotes = MainActivity.NOTES_ARRAY
    private lateinit var mRecyclerView: RecyclerView

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitleView: TextView = itemView.findViewById(R.id.textViewNoteTitle)
        val noteBodyView: TextView = itemView.findViewById(R.id.textViewNoteBody)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val height: Int = parent.measuredHeight / 4
        itemView.minimumHeight = height
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote: Note = mNotes[position]
        holder.noteTitleView.text = currentNote.title
        holder.noteBodyView.text = currentNote.body

        holder.itemView.setOnClickListener {
            val editOrViewIntent = Intent(it.context, CreateNoteActivity::class.java)
            editOrViewIntent.putExtra("NOTE", currentNote)
            editOrViewIntent.putExtra("REQUESTCODE", 2)

            val context = it.context as Activity
            context.startActivityForResult(editOrViewIntent, 2)
        }

        holder.itemView.setOnLongClickListener {
            // add this note to the selection pool
            it.setBackgroundColor(Color.WHITE)
            true
        }
    }

    internal fun setNotes(notes: List<Note>) {
        // TODO: clean the clumsiness
        MainActivity.NOTES_ARRAY = notes as ArrayList<Note>
        mNotes = MainActivity.NOTES_ARRAY
        notifyDataSetChanged()
    }

    override fun getItemCount() = mNotes.size
}