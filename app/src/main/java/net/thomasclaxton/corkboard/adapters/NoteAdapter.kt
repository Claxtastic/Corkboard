package net.thomasclaxton.corkboard.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.activities.CreateNoteActivity
import net.thomasclaxton.corkboard.models.Note
import net.thomasclaxton.corkboard.activities.MainActivity

private const val TAG = "NoteListAdapter"
private const val NOTE = 1
private const val TITLE_ONLY = 2
private const val BODY_ONLY = 3
private var SELECTING: Boolean = false

class NoteListAdapter internal constructor (context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mNotes = MainActivity.NOTES_ARRAY
    private lateinit var mRecyclerView: RecyclerView

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitleView: TextView = itemView.findViewById(R.id.textViewNoteTitle)
        val noteBodyView: TextView = itemView.findViewById(R.id.textViewNoteBody)
    }

    class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitleView: TextView = itemView.findViewById(R.id.textViewNoteTitle)
    }

    class BodyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                return NoteViewHolder(itemView)
            }
            TITLE_ONLY -> {
                val itemView = inflater.inflate(R.layout.recyclerview_title_item, parent, false)
                return TitleViewHolder(itemView)
            }
            BODY_ONLY -> {
                val itemView = inflater.inflate(R.layout.recyclerview_body_item, parent, false)
                return BodyViewHolder(itemView)
            }
            else -> {
                return NoteViewHolder(inflater.inflate(R.layout.recyclerview_note_item, parent, false))
            }
        }
    }

    /** Called on adapter.notifyDataSetChanged() **/
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentNote: Note = mNotes[position]

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

        when (SELECTING) {
            true ->
                holder.itemView.setOnClickListener { currentNote.toggleSelection(holder.itemView) }
            false ->
                holder.itemView.let {
                    (it.findViewById(R.id.cardView) as MaterialCardView).strokeWidth = 0
                    it.setOnClickListener { view ->
                        val editOrViewIntent = Intent(view.context, CreateNoteActivity::class.java)
                        editOrViewIntent.putExtra(view.context.getString(R.string.extras_note), currentNote)
                        editOrViewIntent.putExtra(view.context.getString(R.string.extras_request_code), 2)

                        val context = it.context as Activity
                        context.startActivityForResult(editOrViewIntent, 2)
                    }
                }
        }

//        holder.itemView.setOnLongClickListener {
//            currentNote.toggleSelection(it)
//            // change the MainActivity menu to the selection menu
//            MainActivity.currentMenu = R.menu.menu_select
//            (it.context as Activity).invalidateOptionsMenu()
//
//            // set a flag to change the onClickListener to select notes rather than edit/view
//            SELECTING = true
//            notifyDataSetChanged()
//
//            false
//        }
    }

    fun undoSelections() {
        for (note: Note in MainActivity.NOTES_ARRAY) {
            if (note.isSelected) {
                note.isSelected = false
            }
        }
        SELECTING = false
        notifyDataSetChanged()
    }

    internal fun setNotes(notes: List<Note>) {
        MainActivity.NOTES_ARRAY = notes as ArrayList<Note>
        mNotes = MainActivity.NOTES_ARRAY
        notifyDataSetChanged()
    }

    override fun getItemCount() = mNotes.size
}
