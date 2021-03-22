package net.thomasclaxton.corkboard.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import net.thomasclaxton.corkboard.R
import java.util.*

class CreateReminderActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_create_reminder)
  }

  fun pickDateTime(button: View) {
    val currentDateTime = Calendar.getInstance()
    val startYear = currentDateTime.get(Calendar.YEAR)
    val startMonth = currentDateTime.get(Calendar.MONTH)
    val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
    val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
    val startMinute = currentDateTime.get(Calendar.MINUTE)

    DatePickerDialog(this, { _, year, month, day ->
      TimePickerDialog(this, { _, hour, minute ->
        val pickedDateTime = Calendar.getInstance()
        pickedDateTime.set(year, month, day, hour, minute)
        Toast.makeText(this, "You picked: $pickedDateTime", Toast.LENGTH_SHORT).show()
      }, startHour, startMinute, false).show()
    }, startYear, startMonth, startDay).show()
  }
}