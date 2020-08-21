package net.thomasclaxton.corkboard.models

import java.io.Serializable
import java.util.*

data class NoteListItem(var item: String) : Serializable {

    var uid: String = UUID.randomUUID().toString()
}