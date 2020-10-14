package net.thomasclaxton.corkboard.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.models.Note
import net.thomasclaxton.corkboard.models.NoteList
import net.thomasclaxton.corkboard.models.NoteListItem

private const val TAG = "NoteListItemAdapter"

class NoteListItemAdapter(val context: Context, private val mode: Mode) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mNoteListItems: ArrayList<NoteListItem> = ArrayList()
    private var mNoteListItemStrings: ArrayList<String> = ArrayList()

    enum class Mode {
        CREATE, VIEW
    }

    inner class NoteListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteListItemTextView: TextView = itemView.findViewById(R.id.editTextNoteListItem)
    }

    inner class NoteListItemCreateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteListItemTextView: TextView = itemView.findViewById(R.id.editTextNoteListItem)
        val noteListItemDeleteButton: ImageView = itemView.findViewById(R.id.removeListItemView)

        init {
            noteListItemTextView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (noteListItemTextView.tag != null) {
                        mNoteListItems[noteListItemTextView.tag as Int] = NoteListItem(s.toString())
                        mNoteListItemStrings.add(s.toString())
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (mode) {
            Mode.CREATE -> {
                val itemView = inflater.inflate(R.layout.row_create_note_list_item, parent, false)
                NoteListItemCreateViewHolder(itemView)
            }
            Mode.VIEW -> {
                val itemView = inflater.inflate(R.layout.row_note_list_item, parent, false)
                NoteListItemViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentNoteListItem: NoteListItem = mNoteListItems[position]
        when(mode) {
            Mode.CREATE -> {
                (holder as NoteListItemCreateViewHolder).let {
                    holder.noteListItemTextView.text = currentNoteListItem.item
                    holder.noteListItemTextView.tag = position
                    holder.noteListItemDeleteButton.tag = position
                }
            }
            Mode.VIEW -> {
                (holder as NoteListItemViewHolder).let {
                    holder.noteListItemTextView.text = currentNoteListItem.item
                    holder.noteListItemTextView.tag = position
                }
            }
        }
    }

    fun addItem() {
        mNoteListItems.add(NoteListItem(""))
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        mNoteListItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun setNoteListItems(items: ArrayList<NoteListItem>) {
        mNoteListItems = items
        notifyDataSetChanged()
    }

    fun getNoteListItems(): ArrayList<NoteListItem> { return mNoteListItems }

    override fun getItemCount() = mNoteListItems.size
}
