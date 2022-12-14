package me.soxfox.annote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
        notesList.adapter = NotesAdapter(notes.find(), ::openNote)
    }

    override fun onDestroy() {
        Database.connection.close()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.search -> {
            // Launch search activity
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun openNote(note: Document) {
        // Launch the note editor for a specific document
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("id", note.id)
        startActivity(intent)
    }
}