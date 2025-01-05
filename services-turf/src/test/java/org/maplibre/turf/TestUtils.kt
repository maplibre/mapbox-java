package org.maplibre.turf

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals

object TestUtils {

    fun compareJson(expectedJson: String, actualJson: String) {
        val json = Json { isLenient = true }
        assertEquals(
            json.parseToJsonElement(expectedJson),
            json.parseToJsonElement(actualJson)
        )
    }

    fun loadJsonFixture(filename: String): String {
        val filePath = Path("src/test/resources/$filename")
        return SystemFileSystem.source(filePath).use { rawSource ->
            rawSource.buffered().readString()
        }
    }

    const val DELTA: Double = 1E-10
}
