package net.thomasclaxton.noter.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import net.thomasclaxton.noter.R
import net.thomasclaxton.noter.activities.CreateNoteActivity
import net.thomasclaxton.noter.models.Note
import net.thomasclaxton.noter.activities.MainActivity

private const val TAG = "NoteListAdapter"
private const val NOTE = 1
private const val TITLE_ONLY = 2
private const val BODY_ONLY = 3

class NoteListAdapter internal constructor (context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mNotes = MainActivity.NOTES_ARRAY
    private lateinit var mParent: ViewGroup
    private lateinit var mRecyclerView: RecyclerView

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitleView: TextView = itemView.findViewById(R.id.textViewNoteTitle)
        val noteBodyView: TextView = itemView.findViewById(R.id.textViewNoteBody)
    }

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitleView: TextView = itemView.findViewById(R.id.textViewNoteTitle)
    }

    inner class BodyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteBodyView: TextView = itemView.findViewById(R.id.textViewNoteBody)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getItemViewType(position: Int): Int {
        return if (MainActivity.NOTES_ARRAY[position].body.isEmpty())
            TITLE_ONLY
        else if (MainActivity.NOTES_ARRAY[position].title.isEmpty())
            BODY_ONLY
        else
            NOTE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            NOTE -> {
                val itemView = inflater.inflate(R.layout.recyclerview_note_item, parent, false)
                val height: Int = parent.measuredHeight / 4
                itemView.minimumHeight = height
                return NoteViewHolder(itemView)
            }
            TITLE_ONLY -> {
                val itemView = inflater.inflate(R.layout.recyclerview_title_item, parent, false)
                val height: Int = parent.measuredHeight / 4
                itemView.minimumHeight = height
                return TitleViewHolder(itemView)
            }
            BODY_ONLY -> {
                val itemView = inflater.inflate(R.layout.recyclerview_body_item, parent, false)
                val height: Int = parent.measuredHeight / 4
                itemView.minimumHeight = height
                return BodyViewHolder(itemView)
            }
            else -> {
                return NoteViewHolder(inflater.inflate(R.layout.recyclerview_note_item, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentNote: Note = mNotes[position]
        holder.itemView.let {
            it.setBackgroundColor(ContextCompat.getColor(it.context, R.color.colorDarkBackground))
        }

        when (holder.itemViewType) {
            NOTE ->
                (holder as NoteViewHolder).let {
                    it.noteTitleView.text = currentNote.title
                    it.noteBodyView.text = currentNote.body
                }
            TITLE_ONLY -> {
                (holder as TitleViewHolder).noteTitleView.text = currentNote.title
            }
            BODY_ONLY ->
                (holder as BodyViewHolder).noteBodyView.text = currentNote.body
        }

        holder.itemView.setOnClickListener {
            val editOrViewIntent = Intent(it.context, CreateNoteActivity::class.java)
            editOrViewIntent.putExtra(it.context.getString(R.string.extras_note), currentNote)
            editOrViewIntent.putExtra(it.context.getString(R.string.extras_request_code), 2)

            val context = it.context as Activity
            context.startActivityForResult(editOrViewIntent, 2)
        }

        holder.itemView.setOnLongClickListener {
            // add this note to the selection pool
            currentNote.isSelected = true
            it.setBackgroundColor(Color.WHITE)

            // change the MainActivity menu to the selection menu
            MainActivity.currentMenu = R.menu.menu_select
            (it.context as Activity).invalidateOptionsMenu()

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
