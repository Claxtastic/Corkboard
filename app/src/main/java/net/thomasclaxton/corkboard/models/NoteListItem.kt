package net.thomasclaxton.corkboard.models

import java.io.Serializable
import java.util.*

data class NoteListItem(var item: String) : Serializable {

    var uid: String = UUID.randomUUID().toString()
    var isChecked: Boolean = false

    override fun equals(other: Any?): Boolean {
        return this.item.toLowerCase() == (other as NoteListItem).item.toLowerCase()
    }
}
