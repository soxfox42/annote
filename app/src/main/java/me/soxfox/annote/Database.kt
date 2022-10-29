package me.soxfox.annote

import org.dizitart.no2.Nitrite

// Manages the one instance of Nitrite through the app
object Database {
    lateinit var connection: Nitrite
        private set

    fun open(path: String) {
        connection = Nitrite.builder()
            .compressed()
            .filePath(path)
            .openOrCreate()
    }
}