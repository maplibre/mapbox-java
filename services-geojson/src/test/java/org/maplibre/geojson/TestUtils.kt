package org.maplibre.geojson

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.serialization.json.Json
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal object TestUtils {

    const val DELTA: Double = 1E-10

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

    fun expectNearNumber(expected: Double, actual: Double, epsilon: Double) {
        assertTrue(
            abs(expected - actual) <= epsilon,
            String.format("Expected %f to be near %f", actual, expected),
        )
    }
}
