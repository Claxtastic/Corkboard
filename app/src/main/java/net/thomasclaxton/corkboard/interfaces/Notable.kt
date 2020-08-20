package net.thomasclaxton.corkboard.interfaces

import android.view.View
import java.io.Serializable

interface Notable : Serializable {
    val uid: String
    var isSelected: Boolean

    fun toggleSelection(itemView: View)
}