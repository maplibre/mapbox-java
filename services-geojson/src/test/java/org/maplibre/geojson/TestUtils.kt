package org.maplibre.geojson

import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.util.Scanner

object TestUtils {

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
}
