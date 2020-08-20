package net.thomasclaxton.corkboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.thomasclaxton.corkboard.R
import net.thomasclaxton.corkboard.adapters.NoteListItemAdapter

private const val TAG = "CreateNoteListActivity"

class CreateNoteListActivity : AppCompatActivity() {

    private lateinit var mAdapter: NoteListItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note_list)

        mAdapter = setupRecyclerView()
        mAdapter.addItem()
    }

    private fun setupRecyclerView() : NoteListItemAdapter {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewListItems)
        val adapter = NoteListItemAdapter(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        return adapter
    }

    fun onFabClick(view: View) {
        mAdapter.addItem()
    }

    fun onCheckBoxClick(checkBoxView: View) {
        (checkBoxView as CheckBox).let {
            if (checkBoxView.isChecked) {
                Toast.makeText(applicationContext, "Is checked", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(applicationContext, "Is unchecked", Toast.LENGTH_SHORT).show();
            }
        }
    }
}