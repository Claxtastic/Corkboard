package net.thomasclaxton.noter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "NoteListAdapter"

class NoteListAdapter internal constructor (context: Context)
    : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitleView: TextView = itemView.findViewById(R.id.textViewNoteTitle)
        val noteBodyView: TextView = itemView.findViewById(R.id.textViewNoteBody)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val height: Int = parent.measuredHeight / 4
        itemView.minimumHeight = height
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote: Note = notes[position]
        holder.noteTitleView.text = currentNote.title
        holder.noteBodyView.text = currentNote.body

        holder.itemView.setOnClickListener {
            val editOrViewIntent = Intent(it.context, CreateNoteActivity::class.java)
            editOrViewIntent.putExtra("NOTE", currentNote)
            editOrViewIntent.putExtra("REQUESTCODE", 2)

            val context = it.context as Activity
            context.startActivityForResult(editOrViewIntent, 2)
        }
    }

    internal fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size
}