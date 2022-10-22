package me.soxfox.annote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.activity.addCallback
import org.dizitart.no2.NitriteId

class EditActivity : AppCompatActivity() {
    val title by lazy { findViewById<EditText>(R.id.title) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Fetch notes
        val db = Database.connection
        val notes = db.getCollection("notes")

        val id = intent.getSerializableExtraCompat<NitriteId>("id")
        val note = notes.getById(id)

        title.setText(note.get("title") as? String)

        // Listen for back press
        onBackPressedDispatcher.addCallback(this) {
            note["title"] = title.text.toString()
            notes.update(note)
            finish()
        }

    }
}