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
data class Note(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "body") var body: String
) : Serializable {

    @PrimaryKey
    var uid: String = UUID.randomUUID().toString()

    @Ignore
    var isSelected: Boolean = false

    /** Toggle the selection of this note, and change the border of its' view accordingly **/
    fun toggleSelection(view: View) {
        isSelected = !isSelected
        if (isSelected) {
            (view.findViewById(R.id.cardView) as MaterialCardView).strokeWidth = 4
        } else {
            (view.findViewById(R.id.cardView) as MaterialCardView).strokeWidth = 0
        }
    }
}
