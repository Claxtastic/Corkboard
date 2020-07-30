package net.thomasclaxton.corkboard.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.thomasclaxton.corkboard.models.Note

private val TAG = "AppDatabase"

@Database(entities = [Note::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noter_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(
                        AppDatabaseCallback(
                            scope
                        )
                    )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val noteDao = database.noteDao()
                    noteDao.deleteAll()
                    loadSampleData(noteDao)
                }
            }
        }

        fun loadSampleData(noteDao: NoteDao) {
            var newNote =
                Note("A Title", "A short body")
            noteDao.insert(newNote)
            newNote = Note(
                "To-do: School",
                "Physics \n Bio \n Compsci \n History \n Spanish"
            )
            noteDao.insert(newNote)
            newNote = Note(
                "A third and final note longer title long",
                ""
            )
            noteDao.insert(newNote)
        }
    }
}
