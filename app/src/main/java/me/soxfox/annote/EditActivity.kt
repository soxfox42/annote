package me.soxfox.annote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import org.dizitart.no2.Document
import org.dizitart.no2.NitriteCollection
import org.dizitart.no2.NitriteId

class EditActivity : AppCompatActivity() {
    private val title by lazy { findViewById<EditText>(R.id.title) }
    private val content by lazy { findViewById<EditText>(R.id.content) }

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
        title.addTextChangedListener {
            if (it == null) return@addTextChangedListener
            if (it.isEmpty()) {
                setTitle(getString(R.string.editing_untitled))
            } else {
                setTitle(String.format(getString(R.string.editing), it.toString()))
            }
        }

        content.setText(note["content"] as? String)

        // Listen for back press
        onBackPressedDispatcher.addCallback(this) {
            save()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.save_note -> {
            save()
            finish()
            true
        }
        R.id.delete_note -> {
            notes.remove(note)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun save() {
        note["title"] = title.text.toString()
        note["content"] = content.text.toString()
        notes.update(note)
    }
}