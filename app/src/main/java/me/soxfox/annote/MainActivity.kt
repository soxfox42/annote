package me.soxfox.annote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.dizitart.no2.Nitrite

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Open db connection
        val db = Nitrite.builder()
            .compressed()
            .filePath(filesDir.path + "/annote.db")
            .openOrCreate()

        // Fetch notes
        val notes = db.getCollection("notes")

        // Update note list
        val notesList = findViewById<RecyclerView>(R.id.notesList)
        notesList.adapter = NotesAdapter(notes)
        notesList.layoutManager = LinearLayoutManager(this)
    }
}