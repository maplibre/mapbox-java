package org.maplibre.geojson

import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.nio.charset.StandardCharsets
import java.util.Scanner
import kotlin.math.abs

internal object TestUtils {

    const val DELTA: Double = 1E-10

    fun compareJson(expectedJson: String, actualJson: String) {
        assertEquals(
            JsonParser.parseString(actualJson),
            JsonParser.parseString(expectedJson)
        )
    }

    fun loadJsonFixture(filename: String?): String {
        val classLoader = javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(filename)
        val scanner = Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")
        return if (scanner.hasNext()) scanner.next() else ""
    }

    fun expectNearNumber(expected: Double, actual: Double, epsilon: Double) {
        assertTrue(
            String.format("Expected %f to be near %f", actual, expected),
            abs(expected - actual) <= epsilon
        )
    }
}
