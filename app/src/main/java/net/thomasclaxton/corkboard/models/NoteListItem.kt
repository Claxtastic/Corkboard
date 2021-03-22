package net.thomasclaxton.corkboard.models

import java.io.Serializable
import java.util.*

data class NoteListItem(var item: String, var isChecked: Boolean = false) : Serializable {

  var uid: String = UUID.randomUUID().toString()

  override fun equals(other: Any?): Boolean {
    return this.item.equals((other as NoteListItem).item, ignoreCase = true)
  }
}
