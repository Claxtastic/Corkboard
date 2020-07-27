package net.thomasclaxton.noter.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class Note(
    @ColumnInfo(name="title") var title: String,
    @ColumnInfo(name="body") var body: String
) : Serializable {

    @PrimaryKey
    var uid: String = UUID.randomUUID().toString()

    @Ignore
    var isSelected: Boolean = false
}
