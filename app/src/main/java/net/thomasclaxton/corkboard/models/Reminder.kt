package net.thomasclaxton.corkboard.models

import android.view.View
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import net.thomasclaxton.corkboard.interfaces.Notable
import java.util.*

@Entity
data class Reminder(
        @ColumnInfo(name = "text") var text: String,
        @ColumnInfo(name = "remindTime") var remindTime: String
) : Notable {

    @PrimaryKey
    override val uid: String = UUID.randomUUID().toString()

    @Ignore
    override var isSelected: Boolean = false

    override fun toggleSelection(itemView: View) {
        TODO("Not yet implemented")
    }
}
