package me.soxfox.annote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.dizitart.no2.Document
import org.dizitart.no2.filters.Filters.and

class SearchActivity : AppCompatActivity() {
    private val results by lazy { findViewById<RecyclerView>(R.id.results) }
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        results.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.search, menu)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.isIconified = false
        searchView.maxWidth = Int.MAX_VALUE

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean = false
        })

        return true
    }

    private fun openNote(note: Document) {
        // Launch the note editor for a specific document
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("id", note.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val menu = menu
        if (menu != null) {
            val searchView = menu.findItem(R.id.search).actionView as SearchView
            if (searchView.query.isNotEmpty()) {
                performSearch(searchView.query.toString())
            }
        }
    }

    private fun performSearch(query: String) {
        val filters = query.split(' ').map { AnnoteFilter(it) }

        val notes = Database.connection.getCollection("notes")
        results.adapter = NotesAdapter(notes.find(and(*filters.toTypedArray()))) {
            openNote(it)
        }
    }
}