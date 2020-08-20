package net.thomasclaxton.corkboard.models

import android.view.View
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.material.card.MaterialCardView
import net.thomasclaxton.corkboard.R
import java.io.Serializable
import java.util.*

@Entity
data class NoteList(
    @ColumnInfo(name="title") var title: String,
    @ColumnInfo(name="items") var items: List<String>
) : Serializable {

    @PrimaryKey
    var uid: String = UUID.randomUUID().toString()

    @Ignore
    var isSelected: Boolean = false

    fun toggleSelection(view: View) {
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