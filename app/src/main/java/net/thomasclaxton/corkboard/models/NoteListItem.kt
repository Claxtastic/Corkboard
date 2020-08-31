package net.thomasclaxton.corkboard.models

import java.io.Serializable
import java.util.*

data class NoteListItem(var item: String) : Serializable {

    var uid: String = UUID.randomUUID().toString()

    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false

        return this.item.toLowerCase() == (other as NoteListItem).item.toLowerCase()
    }
}
