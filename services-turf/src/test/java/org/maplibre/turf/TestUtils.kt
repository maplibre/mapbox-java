package org.maplibre.turf

import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

object TestUtils {

    fun compareJson(expectedJson: String, actualJson: String) {
        assertEquals(
            JsonParser.parseString(actualJson),
            JsonParser.parseString(expectedJson)
        )
    }

    fun loadJsonFixture(filename: String): String {
        try {
            val filepath = "src/test/resources/$filename"
            val encoded = Files.readAllBytes(Paths.get(filepath))
            return String(encoded, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            throw IOException("Unable to load test resource $filename")
        }
    }

    const val DELTA: Double = 1E-10
}
