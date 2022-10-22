package me.soxfox.annote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.addCallback
import org.dizitart.no2.Document
import org.dizitart.no2.NitriteCollection
import org.dizitart.no2.NitriteId

class EditActivity : AppCompatActivity() {
    private val title by lazy { findViewById<EditText>(R.id.title) }
    private lateinit var notes: NitriteCollection
    private lateinit var note: Document

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Fetch notes
        val db = Database.connection
        notes = db.getCollection("notes")

        val id = intent.getSerializableExtraCompat<NitriteId>("id")
        note = notes.getById(id)

        val noteTitle = note["title"] as? String
        title.setText(noteTitle)
        setTitle(String.format(getString(R.string.editing), noteTitle))

        // Listen for back press
        onBackPressedDispatcher.addCallback(this) {
            note["title"] = title.text.toString()
            notes.update(note)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
            R.id.delete_note -> {
                notes.remove(note)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}