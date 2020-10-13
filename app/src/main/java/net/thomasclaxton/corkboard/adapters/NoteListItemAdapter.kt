package net.thomasclaxton.corkboard.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.models.NoteListItem

private const val TAG = "NoteListItemAdapter"

class NoteListItemAdapter(val context: Context, private val mode: Mode) :
    RecyclerView.Adapter<NoteListItemAdapter.NoteListItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mNoteListItems: ArrayList<NoteListItem> = ArrayList()
    private var mNoteListItemStrings: ArrayList<String> = ArrayList()

    enum class Mode {
        CREATE, VIEW
    }

    inner class NoteListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteListItemView: TextView = itemView.findViewById(R.id.editTextNoteListItem)

        init {
            noteListItemView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (noteListItemView.tag != null) {
                        mNoteListItems[noteListItemView.tag as Int] = NoteListItem(s.toString())
                        mNoteListItemStrings.add(s.toString())
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListItemViewHolder {
        return when (mode) {
            Mode.CREATE -> {
                val itemView = inflater.inflate(R.layout.row_create_note_list_item, parent, false)
                NoteListItemViewHolder(itemView)
            }
            Mode.VIEW -> {
                val itemView = inflater.inflate(R.layout.row_note_list_item, parent, false)
                NoteListItemViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: NoteListItemViewHolder, position: Int) {
        val currentNoteListItem: NoteListItem = mNoteListItems[position]
        holder.noteListItemView.tag = position
        holder.noteListItemView.text = currentNoteListItem.item
    }

    fun addItem() {
        mNoteListItems.add(NoteListItem(""))
        notifyDataSetChanged()
    }

    fun getNoteListItems(): ArrayList<NoteListItem> { return mNoteListItems }

    fun setNoteListItems(items: ArrayList<NoteListItem>) {
        mNoteListItems = items
        notifyDataSetChanged()
    }

    override fun getItemCount() = mNoteListItems.size
}
