package org.maplibre.turf

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString

actual fun readResourceFile(filename: String): String {
    val filePath = Path("src/commonTest/resources/$filename")
    print(SystemFileSystem.list(Path("src/commonTest/resources/")))
    return SystemFileSystem.source(filePath).use { rawSource ->
        rawSource.buffered().readString()
    }
}