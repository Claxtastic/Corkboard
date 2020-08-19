package net.thomasclaxton.corkboard.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import net.thomasclaxton.corkboard.util.DataConverters
import java.io.Serializable

@Entity
data class List(
    @ColumnInfo(name="title") var title: String
) : Serializable {
}