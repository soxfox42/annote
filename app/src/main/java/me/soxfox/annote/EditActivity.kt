package me.soxfox.annote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import org.dizitart.no2.Document
import org.dizitart.no2.NitriteCollection
import org.dizitart.no2.NitriteId

class EditActivity : AppCompatActivity() {
    private val title by lazy { findViewById<EditText>(R.id.title) }
    private val content by lazy { findViewById<EditText>(R.id.content) }
    private val toggleAttrs by lazy { findViewById<ImageButton>(R.id.toggle_attrs) }
    private val attrs by lazy { findViewById<TableLayout>(R.id.attrs) }
    private val addAttr by lazy { findViewById<Button>(R.id.add_attr) }

    private val fields = mutableListOf<TableRow>()

    private lateinit var notes: NitriteCollection
    private lateinit var note: Document

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Fetch notes
        val db = Database.connection
        notes = db.getCollection("notes")

        // Get current note
        val id = intent.getSerializableExtraCompat<NitriteId>("id")
        note = notes.getById(id)

        // Fill note data
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

        // Fill custom fields
        val attrMap = note["attrs"] as? Map<*, *>
        if (attrMap != null) {
            for (pair in attrMap.asIterable().filterIsInstance<Map.Entry<String, String>>()) {
                val row = layoutInflater.inflate(R.layout.row_attr, attrs, false)
                val delete = row.findViewById<ImageButton>(R.id.delete)
                delete.setOnClickListener {
                    attrs.removeView(row)
                    fields.remove(row)
                }
                attrs.addView(row, attrs.childCount - 1)
                fields.add(row as TableRow)

                row.findViewById<EditText>(R.id.key).setText(pair.key)
                row.findViewById<EditText>(R.id.value).setText(pair.value as? String)
            }
        }

        // Listen for toggle attributes
        toggleAttrs.setOnClickListener {
            it.rotation = 180 - it.rotation
            if (attrs.visibility == View.GONE) {
                attrs.visibility = View.VISIBLE
            } else {
                attrs.visibility = View.GONE
            }
        }

        // Listen for create attribute
        addAttr.setOnClickListener {
            val row = layoutInflater.inflate(R.layout.row_attr, attrs, false)
            val delete = row.findViewById<ImageButton>(R.id.delete)
            delete.setOnClickListener {
                attrs.removeView(row)
                fields.remove(row)
            }
            attrs.addView(row, attrs.childCount - 1)
            fields.add(row as TableRow)
        }

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
        // Remove all attributes, just saving the currently present ones
        note["title"] = title.text.toString()
        note["content"] = content.text.toString()
        val attrs = mutableMapOf<String, String>()
        fields.forEach {
            val key = it.findViewById<EditText>(R.id.key).text.toString()
            val value = it.findViewById<EditText>(R.id.value).text.toString()
            attrs[key] = value
        }
        note["attrs"] = attrs
        notes.update(note)
    }
}