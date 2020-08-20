package net.thomasclaxton.corkboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.models.NoteListItem

class NoteListItemAdapter internal constructor (context: Context) :
    RecyclerView.Adapter<NoteListItemAdapter.NoteListItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mNoteListItems: ArrayList<NoteListItem> = ArrayList()
    private lateinit var mRecyclerView: RecyclerView

    class NoteListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteListItemView: TextView = itemView.findViewById(R.id.editTextNoteListItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListItemViewHolder {
        val itemView = inflater.inflate(R.layout.row_note_list_item, parent, false)
        return NoteListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteListItemViewHolder, position: Int) {
        val currentNoteListItem: NoteListItem = mNoteListItems[position]

        holder.noteListItemView.text = currentNoteListItem.item
    }

    fun addItem() {
        mNoteListItems.add(NoteListItem(""))
        notifyDataSetChanged()
    }

    fun addItem(item: String) {
        mNoteListItems.add(NoteListItem(item))
        notifyDataSetChanged()
    }

    override fun getItemCount() = mNoteListItems.size
}