package net.thomasclaxton.corkboard.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.activities.CreateNoteActivity
import net.thomasclaxton.corkboard.activities.CreateNoteListActivity
import net.thomasclaxton.corkboard.models.Note
import net.thomasclaxton.corkboard.activities.MainActivity
import net.thomasclaxton.corkboard.interfaces.Notable
import net.thomasclaxton.corkboard.models.NoteList
import net.thomasclaxton.corkboard.models.NoteListItem

private const val TAG = "NoteListAdapter"
private const val NOTE = 1
private const val TITLE_ONLY = 2
private const val BODY_ONLY = 3
private const val NOTE_LIST = 4
private var SELECTING: Boolean = false

class NoteListAdapter internal constructor (context: Context) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var mVisibleNotes: ArrayList<Notable> = ArrayList()
  private lateinit var mRecyclerView: RecyclerView

  class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val noteTitleView: TextView = itemView.findViewById(R.id.textViewNoteListTitle)
    val noteBodyView: TextView = itemView.findViewById(R.id.textViewNoteBody)
  }

  class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val noteTitleView: TextView = itemView.findViewById(R.id.textViewNoteListTitle)
  }

  class BodyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val noteBodyView: TextView = itemView.findViewById(R.id.textViewNoteBody)
  }

  class NoteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val noteListTitle: TextView = itemView.findViewById(R.id.textViewNoteListTitle)
    val noteListItemsRecyclerView: RecyclerView = itemView.findViewById(R.id.recyclerViewNoteListItems)
  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    mRecyclerView = recyclerView
  }

  override fun getItemViewType(position: Int): Int {
    return when (mVisibleNotes[position]) {
      is Note -> {
        (mVisibleNotes[position] as Note).let {
          when {
            it.body.isEmpty() -> TITLE_ONLY
            it.title.isEmpty() -> BODY_ONLY
            else -> NOTE
          }
        }
      }
      is NoteList -> return NOTE_LIST
      else -> 0
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    when (viewType) {
      NOTE -> {
        val itemView = inflater.inflate(R.layout.row_note, parent, false)
        return NoteViewHolder(itemView)
      }
      TITLE_ONLY -> {
        val itemView = inflater.inflate(R.layout.row_note_title, parent, false)
        return TitleViewHolder(itemView)
      }
      BODY_ONLY -> {
        val itemView = inflater.inflate(R.layout.row_body, parent, false)
        return BodyViewHolder(itemView)
      }
      NOTE_LIST -> {
        val itemView = inflater.inflate(R.layout.row_note_list, parent, false)
        return NoteListViewHolder(itemView)
      }
      else -> {
        return NoteViewHolder(inflater.inflate(R.layout.row_note, parent, false))
      }
    }
  }

  /** Called on adapter.notifyDataSetChanged() **/
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val currentNotable: Notable = mVisibleNotes[position]

    when (holder.itemViewType) {
      NOTE ->
        (holder as NoteViewHolder).let {
          var note = currentNotable as Note
          it.noteTitleView.text = note.title
          it.noteBodyView.text = note.body
        }
      TITLE_ONLY -> {
        var note = currentNotable as Note
        (holder as TitleViewHolder).noteTitleView.text = note.title
      }
      BODY_ONLY -> {
        var note = currentNotable as Note
        (holder as BodyViewHolder).noteBodyView.text = note.body
      }
      NOTE_LIST -> {
        (holder as NoteListViewHolder).let {
          var noteList = currentNotable as NoteList
          it.noteListTitle.text = noteList.title

          // fill the NoteList's recyclerview with the ListItems
          val context = it.noteListTitle.context
          val noteListItemAdapter = NoteListItemAdapter(context, NoteListItemAdapter.Mode.VIEW)
          it.noteListItemsRecyclerView.adapter = noteListItemAdapter
          it.noteListItemsRecyclerView.layoutManager = LinearLayoutManager(context)
          noteListItemAdapter.setNoteListItems(noteList.items)
        }
      }
    }

    when (SELECTING) {
      true ->
        holder.itemView.setOnClickListener { currentNotable.toggleSelection(holder.itemView) }
      false ->
        holder.itemView.let {
          (it.findViewById(R.id.cardView) as MaterialCardView).strokeWidth = 0
          it.setOnClickListener { view ->
            val context = it.context as Activity
            when (currentNotable) {
              is Note -> {
                val editOrViewIntent = Intent(view.context, CreateNoteActivity::class.java)
                editOrViewIntent.putExtra(view.context.getString(R.string.extras_note), currentNotable)
                editOrViewIntent.putExtra(view.context.getString(R.string.extras_request_code), 2)
                context.startActivityForResult(editOrViewIntent, 2)
              }
              is NoteList -> {
                val editOrViewIntent = Intent(view.context, CreateNoteListActivity::class.java)
                editOrViewIntent.putExtra(view.context.getString(R.string.extras_note), currentNotable)
                editOrViewIntent.putExtra(view.context.getString(R.string.extras_request_code), 2)
                context.startActivityForResult(editOrViewIntent, 2)
              }
            }
          }
        }
    }

//        TODO("Get long click to select working")
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
    for (notable: Notable in MainActivity.ALL_NOTES) {
      if (notable.isSelected) {
        notable.isSelected = false
      }
    }
    SELECTING = false
    notifyDataSetChanged()
  }

  fun setNotes(notes: List<Notable>) {
    mVisibleNotes.clear()
    mVisibleNotes.addAll(notes)
    notifyDataSetChanged()
  }

  fun getNotes(): ArrayList<Notable> { return mVisibleNotes }

  fun filter(query: String?) {
    mVisibleNotes.clear()

    if (query != null) {
      if (query.isEmpty()) {
        mVisibleNotes.addAll(MainActivity.ALL_NOTES)
      } else {
        for (notable in MainActivity.ALL_NOTES) {
          when (notable) {
            is Note -> {
              if (notable.title.toLowerCase().contains(query.toLowerCase()) || notable.body.toLowerCase().contains(query.toLowerCase())) {
                mVisibleNotes.add(notable)
              }
            }
            is NoteList -> {
              if (notable.title.toLowerCase().contains(query.toLowerCase()) || notable.items.contains(NoteListItem(query))) {
                mVisibleNotes.add(notable)
              }
            }
          }
        }
      }
    }
    notifyDataSetChanged()
  }

  override fun getItemCount() = mVisibleNotes.size
}
