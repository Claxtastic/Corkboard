package net.thomasclaxton.corkboard.adapters

import android.content.Context
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_note_list_item.view.*
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.models.NoteListItem

private const val TAG = "NoteListItemAdapter"

class NoteListItemAdapter(val context: Context, private val mode: Mode) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var mNoteListItems: ArrayList<NoteListItem> = ArrayList()

  enum class Mode {
    CREATE, VIEW
  }

  inner class NoteListItemViewModeViewHolder(itemView: View, val adapter: NoteListItemAdapter) : RecyclerView.ViewHolder(itemView) {
    val noteListItemTextView: TextView = itemView.findViewById(R.id.editTextNoteListItem)
    val checkBoxView: CheckBox = itemView.findViewById(R.id.checkBox)
  }

  inner class NoteListItemCreateModeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val noteListItemTextView: TextView = itemView.findViewById(R.id.editTextNoteListItem)
    val noteListItemDeleteButton: ImageView = itemView.findViewById(R.id.removeListItemView)
    val checkBoxView: CheckBox = itemView.findViewById(R.id.checkBox)

    init {
      noteListItemTextView.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
          // TODO debug
          if (noteListItemTextView.tag != null) {
            mNoteListItems[noteListItemTextView.tag as Int] = NoteListItem(s.toString())
          }
        }
      })

      checkBoxView.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
        val noteListItemPos = checkBoxView.tag as Int
        mNoteListItems[noteListItemPos].isChecked = isChecked

        setNoteListItemPaintFlags(isChecked, this)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (mode) {
      Mode.CREATE -> {
        val itemView = inflater.inflate(R.layout.row_create_note_list_item, parent, false)
        NoteListItemCreateModeViewHolder(itemView)
      }
      Mode.VIEW -> {
        val itemView = inflater.inflate(R.layout.row_note_list_item, parent, false)
        NoteListItemViewModeViewHolder(itemView, this)
      }
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val currentNoteListItem: NoteListItem = mNoteListItems[position]
    when(mode) {
      Mode.CREATE -> {
        (holder as NoteListItemCreateModeViewHolder).let {
          holder.noteListItemTextView.text = currentNoteListItem.item
          holder.noteListItemTextView.tag = position
          holder.noteListItemDeleteButton.tag = position
          holder.checkBoxView.tag = position
          if (currentNoteListItem.isChecked) {
            holder.itemView.editTextNoteListItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.checkBoxView.isChecked = true
          }
        }
      }
      Mode.VIEW -> {
        (holder as NoteListItemViewModeViewHolder).let {
          holder.noteListItemTextView.text = currentNoteListItem.item
          holder.noteListItemTextView.tag = position
          holder.checkBoxView.tag = position
          if (currentNoteListItem.isChecked) {
            holder.itemView.editTextNoteListItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.checkBoxView.isChecked = true
          }
        }
      }
    }
  }

  /** Adds item to top **/
  fun addItem() {
    mNoteListItems.add(0, NoteListItem(""))
    notifyItemInserted(0)
  }

  fun handleCheckBoxClick(position: Int, checked: Boolean, holder: RecyclerView.ViewHolder) {
    mNoteListItems[position].isChecked = checked
    setNoteListItemPaintFlags(checked, holder)

    notifyItemChanged(position)
  }

  fun setNoteListItemPaintFlags(checked: Boolean, holder: RecyclerView.ViewHolder) {
    if (checked) {
      holder.itemView.editTextNoteListItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    } else {
      holder.itemView.editTextNoteListItem.paintFlags = 0
    }
  }

  fun removeItem(position: Int) {
    if (position >= mNoteListItems.size)
      mNoteListItems.removeAt(position - 1)
    else
      mNoteListItems.removeAt(position)

    notifyItemRemoved(position)
  }

  fun setNoteListItems(items: ArrayList<NoteListItem>) {
    mNoteListItems.clear()
    mNoteListItems.addAll(items)
    notifyDataSetChanged()
  }

  fun getNoteListItems(): ArrayList<NoteListItem> { return mNoteListItems }

  override fun getItemCount() = mNoteListItems.size
}
