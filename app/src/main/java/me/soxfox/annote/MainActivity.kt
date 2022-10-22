package me.soxfox.annote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.dizitart.no2.Document
import org.dizitart.no2.Document.createDocument

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Open db connection
        Database.open(filesDir.path + "/annote.db")
        val db = Database.connection

        // Fetch notes
        val notes = db.getCollection("notes")

        // Update note list
        val notesList = findViewById<RecyclerView>(R.id.notesList)
        notesList.layoutManager = LinearLayoutManager(this)

        // Listen for FAB press
        val fab = findViewById<FloatingActionButton>(R.id.add_note)
        fab.setOnClickListener {
            val newNote = createDocument("title", "Untitled Note")
            notes.insert(newNote)
            openNote(newNote)
        }
    }

    override fun onResume() {
        super.onResume()

        // Fetch notes
        val db = Database.connection
        val notes = db.getCollection("notes")

        // Update note list
        val notesList = findViewById<RecyclerView>(R.id.notesList)
        notesList.adapter = NotesAdapter(notes, ::openNote)
    }

    override fun onDestroy() {
        val db = Database.connection

        db.commit()
        db.close()

        super.onDestroy()
    }

    fun openNote(note: Document) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("id", note.id)
        startActivity(intent)
    }
}