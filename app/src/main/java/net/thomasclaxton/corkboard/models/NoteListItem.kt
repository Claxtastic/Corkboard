package net.thomasclaxton.corkboard.models

import java.util.*

data class NoteListItem(var item: String) {

    var uid: String = UUID.randomUUID().toString()
}