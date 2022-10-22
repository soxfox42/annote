package me.soxfox.annote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.dizitart.no2.Document
import org.dizitart.no2.NitriteCollection

class NotesAdapter(notes: NitriteCollection) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    private val notes = notes.find().toList()

    override fun getItemCount(): Int = notes.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_row_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val titleView = v.findViewById<TextView>(R.id.title)

        fun bind(note: Document) {
            titleView.text = note.get("title") as? String
        }
    }
}