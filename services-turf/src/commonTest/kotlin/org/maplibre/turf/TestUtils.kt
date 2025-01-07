package org.maplibre.turf

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
        return readResourceFile(filename)
    }

    const val DELTA: Double = 1E-10
}
