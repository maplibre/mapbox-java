package org.maplibre.geojson.turf

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import platform.Foundation.NSBundle

actual fun readResourceFile(filename: String): String {
    val resourcePath = NSBundle.mainBundle.resourcePath!!
    val filePath = Path("${resourcePath}/resources/${filename}")
    return SystemFileSystem.source(filePath).use { rawSource ->
        rawSource.buffered().readString()
    }
}