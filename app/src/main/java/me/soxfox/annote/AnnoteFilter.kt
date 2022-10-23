package me.soxfox.annote

import org.dizitart.no2.Document
import org.dizitart.no2.Filter
import org.dizitart.no2.NitriteId
import org.dizitart.no2.internals.NitriteService
import org.dizitart.no2.store.NitriteMap

enum class FilterMode {
    TEXT, GREATER, LESS, EQUAL,
}

private fun Document.getIntAttr(key: String): Int? {
    val attrMap = this["attrs"] as? Map<*, *> ?: return null
    val attr = attrMap[key] as? String ?: return null
    return attr.toIntOrNull()
}

private fun Document.getStringAttr(key: String): String? {
    val attrMap = this["attrs"] as? Map<*, *> ?: return null
    return attrMap[key] as? String
}

class AnnoteFilter(part: String) : Filter {
    private val filterMode = when {
        !part.contains(':') -> FilterMode.TEXT
        part.contains(":>") -> FilterMode.GREATER
        part.contains(":<") -> FilterMode.LESS
        else -> FilterMode.EQUAL
    }

    private val key = when (filterMode) {
        FilterMode.TEXT -> ""
        else -> part.split(':')[0]
    }

    private val value = when (filterMode) {
        FilterMode.TEXT -> part
        FilterMode.GREATER -> part.split(":>")[1]
        FilterMode.LESS -> part.split(":<")[1]
        FilterMode.EQUAL -> part.split(':')[1]
    }

    override fun apply(documentMap: NitriteMap<NitriteId, Document>): MutableSet<NitriteId> =
        when (filterMode) {
            FilterMode.TEXT -> documentMap.entrySet().filter { (_, doc) ->
                doc["title"].toString().lowercase().contains(value.lowercase()) ||
                        doc["content"].toString().lowercase().contains(value.lowercase())
            }.map { it.key }.toMutableSet()
            FilterMode.GREATER -> documentMap.entrySet().filter { (_, doc) ->
                doc.getIntAttr(key)?.let { it > (value.toIntOrNull() ?: return@filter false) }
                    ?: false
            }.map { it.key }.toMutableSet()
            FilterMode.LESS -> documentMap.entrySet().filter { (_, doc) ->
                doc.getIntAttr(key)?.let { it < (value.toIntOrNull() ?: return@filter false) }
                    ?: false
            }.map { it.key }.toMutableSet()
            FilterMode.EQUAL -> documentMap.entrySet().filter { (_, doc) ->
                doc.getStringAttr(key)?.let { it.lowercase() == value.lowercase() } ?: false
            }.map { it.key }.toMutableSet()
        }

    override fun setNitriteService(nitriteService: NitriteService) = Unit
}