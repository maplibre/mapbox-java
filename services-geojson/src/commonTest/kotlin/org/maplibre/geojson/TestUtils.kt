package org.maplibre.geojson

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
        return readResourceFile(filename)
    }

    fun expectNearNumber(expected: Double, actual: Double, epsilon: Double) {
        assertTrue(
            abs(expected - actual) <= epsilon,
            "Expected $actual to be near $expected",
        )
    }
}
