package me.soxfox.annote

import org.dizitart.no2.Nitrite

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