package net.thomasclaxton.noter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note(
    @ColumnInfo(name="title") var title: String,
    @ColumnInfo(name="body") var body: String
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}
