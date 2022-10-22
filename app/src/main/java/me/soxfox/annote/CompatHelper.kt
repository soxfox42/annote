package me.soxfox.annote

import android.content.Intent
import android.os.Build
import java.io.Serializable

// Extension function to deal with API 33 change to getSerializableExtra
inline fun <reified T : Serializable> Intent.getSerializableExtraCompat(key: String): T? = when {
    // New API, use new version of getSerializableExtra
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
        key,
        T::class.java
    )
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as T?
}