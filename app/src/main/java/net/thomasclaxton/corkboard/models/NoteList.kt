package net.thomasclaxton.corkboard.models

import android.view.View
import androidx.room.*
import net.thomasclaxton.corkboard.interfaces.Notable
import net.thomasclaxton.corkboard.util.DataConverters
import java.io.Serializable
import java.util.*

@Entity
data class NoteList(
    @ColumnInfo(name="title") var title: String,

    @ColumnInfo(name="items")
    @TypeConverters(DataConverters::class)
    var items: ArrayList<NoteListItem>
) : Notable {

    @PrimaryKey
    override var uid: String = UUID.randomUUID().toString()

    @Ignore
    override var isSelected: Boolean = false

    override fun toggleSelection(view: View) {
//        isSelected = !isSelected
//        if (isSelected) {
//            (view.findViewById(R.id.cardView) as MaterialCardView).strokeWidth = 4
//        } else {
//            (view.findViewById(R.id.cardView) as MaterialCardView).strokeWidth = 0
//        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        return this.uid == (other as NoteList).uid
    }
}